package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.BookingDoneException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Candidate business logic.
 */
@Service
public class CandidateService {
  public static final String CANDIDATE_TIME_SLOT = "CandidateTimeSlot";
  public static final String ID = "Id";
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   *
   * @param candidateTimeSlotRepository candidate time slot repository
   * @param bookingRepository           booking repository
   */
  public CandidateService(CandidateTimeSlotRepository candidateTimeSlotRepository,
                          BookingRepository bookingRepository) {
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Validate period for candidate time slot.
   *
   * @param from start time
   * @param to   end time
   */
  public void validatePeriod(LocalTime from, LocalTime to, LocalDate date) {
    if (date.isBefore(LocalDate.now())) {
      throw new InvalidPeriodException(
          "Date " + date + " is outdated");
    }

    if (to.isBefore(from)) {
      throw new InvalidPeriodException("Period start " + from + " should be less than end " + to);
    }

    if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
      throw new InvalidPeriodException(
          "Period for candidate`s slot should be rounded to 30 minutes");
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) < 90) {
      throw new InvalidPeriodException(
          "Period for candidate`s slot should be more or equal to 1.5h");
    }
  }

  /**
   * Create candidate time slot.
   *
   * @param candidateTimeSlot candidate time slot object
   * @return candidate time slot object if success
   */
  public CandidateTimeSlot createSlot(CandidateTimeSlot candidateTimeSlot) {
    LocalTime from = candidateTimeSlot.getFrom();
    LocalTime to = candidateTimeSlot.getTo();
    LocalDate date = candidateTimeSlot.getDate();

    validatePeriod(from, to, date);

    return candidateTimeSlotRepository.save(candidateTimeSlot);
  }

  /**
   * Update candidate time slot.
   *
   * @param candidateTimeSlot candidateTimeSlot candidate time slot object
   * @param candidateSlotId   candidateTimeSlot id
   * @return candidate time slot object if success
   */
  public CandidateTimeSlot updateSlot(CandidateTimeSlot candidateTimeSlot, UUID candidateSlotId) {

    CandidateTimeSlot existingSlot =
        candidateTimeSlotRepository.findById(candidateSlotId).orElseThrow(
            () -> new ResourceNotFoundException(CANDIDATE_TIME_SLOT, ID, candidateSlotId)
        );

    // Check if there is no bookings with this candidate slot
    List<Booking> bookings = bookingRepository.getBookingsByCandidateSlotId(existingSlot.getId());

    if (!bookings.isEmpty()) {
      throw new BookingDoneException(CANDIDATE_TIME_SLOT, ID, existingSlot.getId());
    }

    LocalTime from = candidateTimeSlot.getFrom();
    LocalTime to = candidateTimeSlot.getTo();

    LocalDate date = candidateTimeSlot.getDate();

    validatePeriod(from, to, date);

    existingSlot.setFrom(candidateTimeSlot.getFrom());
    existingSlot.setTo(candidateTimeSlot.getTo());
    existingSlot.setDate(candidateTimeSlot.getDate());

    return candidateTimeSlotRepository.save(existingSlot);
  }

  //  /**
  //   * List slots of candidate.
  //   *
  //   * @param candidateId candidate time slot id
  //   * @return list of candidate`s slots
  //   */
  //  public List<CandidateTimeSlot> getSlotsByCandidateId(UUID candidateId) {
  //    Optional<User> candidate = userRepository.findById(candidateId);
  //    if (candidate.isEmpty()) {
  //      throw new ResourceNotFoundException(CANDIDATE, ID, candidateId);
  //    }
  //
  //    return candidateTimeSlotRepository.getCandidateSlotsByCandidateId(candidateId);
  //  }

  /**
   * Delete candidate time slot.
   *
   * @param slotId candidate time slot id
   */
  public void deleteSlot(UUID slotId) {
    Optional<CandidateTimeSlot> slot = candidateTimeSlotRepository.findById(slotId);
    if (slot.isEmpty()) {
      throw new ResourceNotFoundException(CANDIDATE_TIME_SLOT, ID, slotId);
    }
    candidateTimeSlotRepository.deleteById(slotId);
  }

}
