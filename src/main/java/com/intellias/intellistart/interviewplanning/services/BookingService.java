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
import java.time.Duration;
import java.time.LocalTime;
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
   * @param interviewerSlotId       Interviewer time slot id
   * @param candidateTimeSlotId     Candidate time slot id
   * @param from                    start time
   * @param to                      end time
   * @param subject                 Subject of booking
   * @param description             Description of booking
   * @return                        saved Booking
   */
  public Booking createBooking(UUID interviewerSlotId,
      UUID candidateTimeSlotId,
      LocalTime from,
      LocalTime to,
      String subject,
      String description) {

    validateBookingFields(interviewerSlotId, candidateTimeSlotId, from, to, subject, description);

    Booking booking = new Booking(
        from, to, interviewerSlotId, candidateTimeSlotId, subject, description
    );

    bookingRepository.save(booking);

    return  booking;
  }



  /**
   * Gets Booking by Id.
   *
   * @param id Booking id
   * @return Booking
   */
  public Booking getBookingById(UUID id) {
    return bookingRepository.findById(id).orElseThrow(
        () -> new NoSuchElementException("Booking with id " + id + " wasn't found"));
  }

  /**
   * Updates Booking.
   *
   * @param updatedBooking        Updated Booking
   */
  @Transactional
  public void updateBooking(UUID id, Booking updatedBooking) {

    Booking curBooking = bookingRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Booking to update with id " + id + " wasn't found"));

    validateBookingFields(
        updatedBooking.getInterviewerTimeSlotId(),
        updatedBooking.getCandidateTimeSlotId(),
        updatedBooking.getFrom(),
        updatedBooking.getTo(),
        updatedBooking.getSubject(),
        updatedBooking.getDescription()
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
      LocalTime to,
      String subject,
      String description) {

    isInterviewerLimitExceeded(interviewerSlotId);

    boolean existCandidateTimeSlot = candidateTimeSlotRepository.existsById(candidateTimeSlotId);
    if (!existCandidateTimeSlot) {
      throw new IllegalStateException(
          "Candidate Time slot with id " + candidateTimeSlotId + " does not exists");
    }

    if (!subject.isBlank() && subject.length() > 255) {
      throw new IllegalStateException(
          "Subject cannot be empty and must be less than 255 characters");
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) != 90) {
      throw new InvalidPeriodException(
          "Booking duration must equal to 1.5h");
    }

    if (!description.isBlank() && description.length() > 4000) {
      throw new IllegalStateException(
          "Description cannot be empty and must be less than 4000 characters");
    }

  }

  /**
   * Checks if interviewer's limit wasn't exceeded.
   *
   * @param interviewerTimeSlotId Interviewer time slot id
   * @return false, when limit wasn't exceeded
   */
  private boolean isInterviewerLimitExceeded(UUID interviewerTimeSlotId) {
    UUID interviewerId = interviewerTimeSlotRepository.findById(interviewerTimeSlotId)
        .orElseThrow(
            () -> new IllegalStateException(
                "Interviewer Time slot with id " + interviewerTimeSlotId + " does not exists"))
        .getInterviewerId();
    InterviewerBookingLimit interviewerBookingLimit = interviewerBookingLimitRepository
        .findByInterviewerId(interviewerId).orElseThrow();
    int bookingLimit = interviewerBookingLimit.getWeekBookingLimit();
    int bookingCount = interviewerBookingLimit.getCurrentBookingCount();
    if (bookingCount >= bookingLimit) {
      throw new ValidationException(
          ExceptionMessage.INTERVIEWER_BOOKING_LIMIT_EXCEEDED.getMessage());
    }
    return false;

  }
}
