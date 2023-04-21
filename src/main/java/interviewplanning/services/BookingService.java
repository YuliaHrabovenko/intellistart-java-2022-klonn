package interviewplanning.services;


import static interviewplanning.utils.PeriodUtil.isBookingsOverlapping;
import static interviewplanning.utils.WeekUtil.getFirstDateOfWeekByYearWeekNum;

import interviewplanning.exceptions.NotFoundException;
import interviewplanning.exceptions.ValidationException;
import interviewplanning.models.Booking;
import interviewplanning.models.CandidateTimeSlot;
import interviewplanning.models.InterviewerBookingLimit;
import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.repositories.BookingRepository;
import interviewplanning.repositories.CandidateTimeSlotRepository;
import interviewplanning.repositories.InterviewerBookingLimitRepository;
import interviewplanning.repositories.InterviewerTimeSlotRepository;
import interviewplanning.utils.PeriodUtil;
import interviewplanning.utils.WeekUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Booking business logic.
 */
@Service
public class BookingService {

  private final BookingRepository bookingRepository;
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final InterviewerBookingLimitRepository interviewerBookingLimitRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;


  /**
   * Constructor.
   *
   * @param bookingRepository                 Booking repository
   * @param interviewerTimeSlotRepository     Interviewer time slot repository
   * @param interviewerBookingLimitRepository Interviewer booking limit repository
   * @param candidateTimeSlotRepository       Candidate time slot repository
   */
  @Autowired
  public BookingService(BookingRepository bookingRepository,
      InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      InterviewerBookingLimitRepository interviewerBookingLimitRepository,
      CandidateTimeSlotRepository candidateTimeSlotRepository) {
    this.bookingRepository = bookingRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.interviewerBookingLimitRepository = interviewerBookingLimitRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;

  }


  /**
   * Creates booking: accepts Booking parameters, checks it, saves Booking and returns it.
   *
   * @param interviewerSlotId   Interviewer time slot id
   * @param candidateTimeSlotId Candidate time slot id
   * @param from                start time
   * @param to                  end time
   * @param subject             Subject of booking
   * @param description         Description of booking
   * @return saved Booking
   */
  public Booking createBooking(UUID interviewerSlotId,
      UUID candidateTimeSlotId,
      LocalTime from,
      LocalTime to,
      String subject,
      String description) {

    validateBookingFields(interviewerSlotId, candidateTimeSlotId, from, to);

    List<Booking> bookings = bookingRepository.findByInterviewerTimeSlotId(interviewerSlotId);
    isBookingsOverlapping(from, to, bookings);

    Booking booking = new Booking(
        from, to, interviewerSlotId, candidateTimeSlotId, subject, description
    );

    bookingRepository.save(booking);



    return booking;
  }

  /**
   * Updates Booking.
   *
   * @param updatedBooking Updated Booking
   */
  @Transactional
  public Booking updateBooking(UUID id, Booking updatedBooking) {

    Booking curBooking = bookingRepository.findById(id)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.BOOKING_NOT_FOUND));

    curBooking.setCandidateTimeSlotId(null);

    validateBookingFields(
        updatedBooking.getInterviewerTimeSlotId(),
        updatedBooking.getCandidateTimeSlotId(),
        updatedBooking.getFrom(),
        updatedBooking.getTo()
    );

    List<Booking> bookings = bookingRepository.findByInterviewerTimeSlotId(
        curBooking.getInterviewerTimeSlotId());
    bookings.remove(curBooking);
    isBookingsOverlapping(curBooking.getFrom(), curBooking.getTo(), bookings);

    curBooking.setInterviewerTimeSlotId(updatedBooking.getInterviewerTimeSlotId());
    curBooking.setCandidateTimeSlotId(updatedBooking.getCandidateTimeSlotId());
    curBooking.setFrom(updatedBooking.getFrom());
    curBooking.setTo(updatedBooking.getTo());
    curBooking.setSubject(updatedBooking.getSubject());
    curBooking.setDescription(updatedBooking.getDescription());
    return curBooking;
  }

  /**
   * Deletes Booking.
   *
   * @param bookingId booking id
   */
  public void deleteBooking(UUID bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.BOOKING_NOT_FOUND));
    bookingRepository.delete(booking);
  }

  private void validateBookingFields(UUID interviewerSlotId,
      UUID candidateTimeSlotId,
      LocalTime from,
      LocalTime to) {

    isInterviewerLimitExceeded(interviewerSlotId);

    boolean existCandidateTimeSlot = candidateTimeSlotRepository.existsById(candidateTimeSlotId);
    if (!existCandidateTimeSlot) {
      throw new NotFoundException(NotFoundException.CANDIDATE_SLOT_NOT_FOUND);
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) != 90) {
      throw new ValidationException(ValidationException.WRONG_BOOKING_DURATION);
    }

    PeriodUtil.validatePeriod(from, to);

    CandidateTimeSlot cts = candidateTimeSlotRepository.findById(candidateTimeSlotId).get();
    LocalTime ctsFrom = cts.getFrom();
    LocalTime ctsTo = cts.getTo();

    if (from.isBefore(ctsFrom) || to.isAfter(ctsTo)) {
      throw new ValidationException(ValidationException.BOOKING_OUT_OF_BOUNDS_CANDIDATE);
    }

    InterviewerTimeSlot its = interviewerTimeSlotRepository.findById(interviewerSlotId).get();
    LocalTime itsFrom = its.getFrom();
    LocalTime itsTo = its.getTo();

    if (from.isBefore(itsFrom) || to.isAfter(itsTo)) {
      throw new ValidationException(ValidationException.BOOKING_OUT_OF_BOUNDS_INTERVIEWER);
    }

    if (!bookingRepository.findByCandidateTimeSlotId(candidateTimeSlotId).isEmpty()) {
      throw new ValidationException(ValidationException.CANDIDATE_SLOT_BOOKED);
    }

    int itsYear = Integer.parseInt(its.getWeekNum().substring(0, 4));
    int itsWeek = Integer.parseInt(its.getWeekNum().substring(4));

    LocalDate itsDate = getFirstDateOfWeekByYearWeekNum(itsYear, itsWeek).plusDays(
        (its.getDayOfWeek().getValue() - 1));

    if (!itsDate.equals(cts.getDate())) {
      throw new ValidationException(ValidationException.DIFFERENT_SLOTS_DATES);
    }


  }

  private boolean isInterviewerLimitExceeded(UUID interviewerTimeSlotId) {
    //Getting BookingLimitRepository
    UUID interviewerId = interviewerTimeSlotRepository.findById(interviewerTimeSlotId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.INTERVIEWER_SLOT_NOT_FOUND))
        .getInterviewerId();
    List<InterviewerBookingLimit> interviewerBookingLimits = interviewerBookingLimitRepository
        .findByInterviewerId(interviewerId);
    InterviewerBookingLimit isNextWeekLimitExist = interviewerBookingLimitRepository
        .findByInterviewerIdAndWeekNum(interviewerId,
            WeekUtil.getNextWeekNumber());

    int counter = interviewerBookingLimits.size();
    if (isNextWeekLimitExist != null) {
      counter--;
    }
    //If there are no limits
    if (counter == 0) {
      return false;
    }
    //Sorting the list and getting first (not next week) limit
    interviewerBookingLimits.sort(
        Comparator.comparingInt(o -> Integer.parseInt(o.getWeekNum())));
    InterviewerBookingLimit interviewerBookingLimit = interviewerBookingLimits.get(counter - 1);



    int bookingLimit = interviewerBookingLimit.getWeekBookingLimit();
    int bookingCount = interviewerBookingLimit.getCurrentBookingCount();

    if (bookingCount >= bookingLimit) {
      throw new ValidationException(ValidationException.INTERVIEWER_BOOKING_LIMIT_EXCEEDED);
    }
    return false;

  }
}


