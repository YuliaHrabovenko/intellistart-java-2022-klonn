package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.PeriodUtil;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.Duration;
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

  @Autowired
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

    Booking booking = new Booking(
        from, to, interviewerSlotId, candidateTimeSlotId, subject, description
    );

    bookingRepository.save(booking);

    return booking;
  }


  /**
   * Gets Booking by Id.
   *
   * @param id Booking id
   * @return Booking
   */
  public Booking getBookingById(UUID id) {
    return bookingRepository.findById(id).orElseThrow(
        () -> new NotFoundException(ExceptionMessage.BOOKING_NOT_FOUND.getMessage()));
  }

  /**
   * Updates Booking.
   *
   * @param updatedBooking Updated Booking
   */
  @Transactional
  public void updateBooking(UUID id, Booking updatedBooking) {

    Booking curBooking = bookingRepository.findById(id)
        .orElseThrow(
            () -> new NotFoundException(ExceptionMessage.BOOKING_NOT_FOUND.getMessage()));

    validateBookingFields(
        updatedBooking.getInterviewerTimeSlotId(),
        updatedBooking.getCandidateTimeSlotId(),
        updatedBooking.getFrom(),
        updatedBooking.getTo()
    );

    curBooking.setInterviewerTimeSlotId(updatedBooking.getInterviewerTimeSlotId());
    curBooking.setCandidateTimeSlotId(updatedBooking.getCandidateTimeSlotId());
    curBooking.setFrom(updatedBooking.getFrom());
    curBooking.setTo(updatedBooking.getTo());
    curBooking.setSubject(updatedBooking.getSubject());
    curBooking.setDescription(updatedBooking.getDescription());

  }

  /**
   * Deletes Booking.
   *
   * @param bookingId booking id
   */

  public void deleteBooking(UUID bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(
            () -> new NotFoundException(ExceptionMessage.BOOKING_NOT_FOUND.getMessage()));
    bookingRepository.delete(booking);
  }

  private void validateBookingFields(UUID interviewerSlotId,
      UUID candidateTimeSlotId,
      LocalTime from,
      LocalTime to) {

    isInterviewerLimitExceeded(interviewerSlotId);

    boolean existCandidateTimeSlot = candidateTimeSlotRepository.existsById(candidateTimeSlotId);
    if (!existCandidateTimeSlot) {
      throw new NotFoundException(ExceptionMessage.CANDIDATE_SLOT_NOT_FOUND.getMessage());
    }

    PeriodUtil.validatePeriod(from, to);

    if (Math.abs(Duration.between(from, to).toMinutes()) != 90) {
      throw new ValidationException(
          ExceptionMessage.WRONG_BOOKING_DURATION.getMessage());
    }


  }

  private boolean isInterviewerLimitExceeded(UUID interviewerTimeSlotId) {
    //Getting BookingLimitRepository
    UUID interviewerId = interviewerTimeSlotRepository.findById(interviewerTimeSlotId)
        .orElseThrow(
            () -> new NotFoundException(ExceptionMessage.INTERVIEWER_SLOT_NOT_FOUND.getMessage()))
        .getInterviewerId();
    List<InterviewerBookingLimit> interviewerBookingLimits = interviewerBookingLimitRepository
        .findByInterviewerId(interviewerId).orElseThrow();
    InterviewerBookingLimit isNextWeekLimitExist = interviewerBookingLimitRepository
        .findInterviewerBookingLimitByInterviewerIdAndWeekNum(interviewerId,
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
      throw new ValidationException(
          ExceptionMessage.INTERVIEWER_BOOKING_LIMIT_EXCEEDED.getMessage());
    }
    return false;

  }
}
