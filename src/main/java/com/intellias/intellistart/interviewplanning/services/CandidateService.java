package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.BookingDoneException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Period;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
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
  public static final String PERIOD = "Period";
  public static final String CANDIDATE = "Candidate";
  private final UserRepository userRepository;
  private final PeriodRepository periodRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   *
   * @param candidateRepository         candidate repository
   * @param periodRepository            period repository
   * @param candidateTimeSlotRepository candidate time slot repository
   * @param bookingRepository           booking repository
   */
  public CandidateService(UserRepository candidateRepository,
                          PeriodRepository periodRepository,
                          CandidateTimeSlotRepository candidateTimeSlotRepository,
                          BookingRepository bookingRepository) {
    this.userRepository = candidateRepository;
    this.periodRepository = periodRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Validate period for candidate time slot.
   *
   * @param from start time
   * @param to   end time
   */
  public void validatePeriod(LocalDateTime from, LocalDateTime to) {
    if (from.isBefore(LocalDateTime.now()) || to.isBefore(LocalDateTime.now())) {
      throw new InvalidPeriodException(
          "Period with start " + from + " and end " + to + " is outdated");
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
    Period period = periodRepository.findById(candidateTimeSlot.getPeriodId()).orElseThrow(
        () -> new ResourceNotFoundException(PERIOD, ID, candidateTimeSlot.getPeriodId())
    );

    LocalDateTime from = period.getFrom();
    LocalDateTime to = period.getTo();

    validatePeriod(from, to);

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
    List<Booking> bookings = bookingRepository.getBookingsByCandidateSlotId(candidateSlotId);

    if (!bookings.isEmpty()) {
      throw new BookingDoneException(CANDIDATE_TIME_SLOT, ID, candidateSlotId);
    }

    // Check if new period of the candidate slot is valid
    Period period = periodRepository.findById(candidateTimeSlot.getPeriodId()).orElseThrow(
        () -> new ResourceNotFoundException(PERIOD, ID, candidateTimeSlot.getPeriodId())
    );
    LocalDateTime from = period.getFrom();
    LocalDateTime to = period.getTo();

    validatePeriod(from, to);

    existingSlot.setPeriodId(candidateTimeSlot.getPeriodId());

    return candidateTimeSlotRepository.save(existingSlot);
  }

  /**
   * List slots of candidate.
   *
   * @param candidateId candidate time slot id
   * @return list of candidate`s slots
   */
  public List<CandidateTimeSlot> getSlotsByCandidateId(UUID candidateId) {
    Optional<User> candidate = userRepository.findById(candidateId);
    if (candidate.isEmpty()) {
      throw new ResourceNotFoundException(CANDIDATE, ID, candidateId);
    }

    return candidateTimeSlotRepository.getCandidateSlotsByCandidateId(candidateId);
  }

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
