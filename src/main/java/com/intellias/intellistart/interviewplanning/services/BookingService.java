package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerBookingLimitExceededException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.BookingStatus;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
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
  private final PeriodRepository periodRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;

  /**
   * Constructor.
   *
   * @param bookingRepository                 Booking repository
   * @param interviewerTimeSlotRepository     Interviewer time slot repository
   * @param interviewerBookingLimitRepository Interviewer booking limit repository
   * @param periodRepository                  Period repository
   * @param candidateTimeSlotRepository       Candidate time slot repository
   */
  @Autowired
  public BookingService(BookingRepository bookingRepository,
      InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      InterviewerBookingLimitRepository interviewerBookingLimitRepository,
      PeriodRepository periodRepository,
      CandidateTimeSlotRepository candidateTimeSlotRepository) {
    this.bookingRepository = bookingRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.interviewerBookingLimitRepository = interviewerBookingLimitRepository;
    this.periodRepository = periodRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;

  }

  /**
   * Creates booking: accepts Booking, checks if interviewer hasn't exceeded his weekly limit of
   * Bookings, saves Booking and returns it.
   *
   * @param booking Booking
   * @return Booking
   */
  public Booking createBooking(Booking booking) {
    //InterviewerTimeSlotId
    UUID id = booking.getInterviewerTimeSlotId();

    isInterviewerLimitExceeded(id);

    return bookingRepository.save(booking);
  }

  /**
   * Gets Booking by Id.
   *
   * @param id Booking id
   * @return Booking
   */
  public Booking getBookingById(UUID id) {
    return bookingRepository.findById(id).orElseThrow();
  }

  /**
   * Updates booking.
   *
   * @param id                    Id of Booking
   * @param periodId              Period id
   * @param interviewerTimeSlotId Interviewer time slot id
   * @param candidateTimeSlotId   Candidate time slot id
   * @param status                Status of Booking
   * @param subject               Subject of Booking
   * @param description           Description of Booking
   */
  @Transactional
  public void updateBooking(UUID id,
      UUID periodId,
      UUID interviewerTimeSlotId,
      UUID candidateTimeSlotId,
      BookingStatus status,
      String subject,
      String description) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Booking to update wasn't found"));
    if (periodId != null && !Objects.equals(booking.getPeriodId(), periodId)
        && periodRepository.existsById(periodId)) {
      booking.setPeriodId(periodId);
    }
    if (interviewerTimeSlotId != null
        && !Objects.equals(booking.getInterviewerTimeSlotId(), interviewerTimeSlotId)
        && interviewerTimeSlotRepository.existsById(interviewerTimeSlotId)
        && !isInterviewerLimitExceeded(interviewerTimeSlotId)) {
      booking.setInterviewerTimeSlotId(interviewerTimeSlotId);
    }
    if (candidateTimeSlotId != null
        && !Objects.equals(booking.getCandidateTimeSlotId(), candidateTimeSlotId)
        && candidateTimeSlotRepository.existsById(candidateTimeSlotId)) {
      booking.setCandidateTimeSlotId(candidateTimeSlotId);
    }
    if (status != null && !Objects.equals(booking.getStatus(), status)) {
      booking.setStatus(status);
    }
    if (subject != null
        && !Objects.equals(booking.getSubject(), subject)
        && !subject.isBlank() && subject.length() > 0) {
      booking.setSubject(subject);
    }
    if (description != null
        && !Objects.equals(booking.getDescription(), description)
        && !description.isBlank() && description.length() > 0) {
      booking.setDescription(description);
    }
  }

  /**
   * Deletes Booking.
   *
   * @param id Id of Booking
   */
  public void deleteBooking(UUID id) {
    if (!bookingRepository.existsById(id)) {
      throw new NoSuchElementException("Booking to delete wasn't found");
    }
    bookingRepository.deleteById(id);
  }

  /**
   * Checks if interviewer's limit wasn't exceeded.
   *
   * @param interviewerTimeSlotId Interviewer time slot id
   * @return false, when limit wasn't exceeded
   */

  private boolean isInterviewerLimitExceeded(UUID interviewerTimeSlotId) {
    UUID interviewerId = interviewerTimeSlotRepository.findById(interviewerTimeSlotId)
        .orElseThrow().getInterviewerId();
    InterviewerBookingLimit interviewerBookingLimit = interviewerBookingLimitRepository
        .findByInterviewerId(interviewerId).orElseThrow();
    int bookingLimit = interviewerBookingLimit.getWeekBookingLimit();
    int bookingCount = interviewerBookingLimit.getCurrentBookingCount();
    if (bookingCount >= bookingLimit) {
      throw new InterviewerBookingLimitExceededException();
    }
    return false;

  }
}