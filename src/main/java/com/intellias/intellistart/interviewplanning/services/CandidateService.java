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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Candidate business logic.
 */
@Service
public class CandidateService {
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
    Optional<Period> period = periodRepository.findById(candidateTimeSlot.getPeriodId());
    if (period.isEmpty()) {
      throw new ResourceNotFoundException("Period", "Id", candidateTimeSlot.getPeriodId());
    }

    Optional<User> candidate = userRepository.findById(candidateTimeSlot.getCandidateId());
    if (candidate.isEmpty()) {
      throw new ResourceNotFoundException("Candidate", "Id", candidateTimeSlot.getCandidateId());
    }

    LocalDateTime from = period.get().getFrom();
    LocalDateTime to = period.get().getTo();

    validatePeriod(from, to);

    return candidateTimeSlotRepository.save(candidateTimeSlot);
  }

  /**
   * Update candidate time slot.
   *
   * @param candidateTimeSlot candidate time slot object
   * @return candidate time slot object if success
   */
  public CandidateTimeSlot updateSlot(CandidateTimeSlot candidateTimeSlot) {
    Optional<CandidateTimeSlot> slot =
        candidateTimeSlotRepository.findById(candidateTimeSlot.getId());
    if (slot.isEmpty()) {
      throw new ResourceNotFoundException("CandidateTimeSlot", "Id", candidateTimeSlot.getId());
    }

    List<Booking> bookings = bookingRepository.findAll();
    for (Booking booking : bookings) {
      if (booking.getCandidateTimeSlotId().equals(candidateTimeSlot.getId())) {
        throw new BookingDoneException("CandidateTimeSlot", "Id", candidateTimeSlot.getId());
      }
    }

    return candidateTimeSlotRepository.save(candidateTimeSlot);
  }

  /**
   * Delete candidate time slot.
   *
   * @param id id of candidate time slot
   */
  public void deleteSlot(UUID id) {
    Optional<CandidateTimeSlot> slot = candidateTimeSlotRepository.findById(id);
    if (slot.isEmpty()) {
      throw new ResourceNotFoundException("CandidateTimeSlot", "Id", id);
    }
    candidateTimeSlotRepository.deleteById(id);
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
      throw new ResourceNotFoundException("Candidate", "Id", candidateId);
    }

    List<CandidateTimeSlot> allCandidateSlots = candidateTimeSlotRepository.findAll();
    List<CandidateTimeSlot> candidateTimeSlots = new ArrayList<>();

    for (CandidateTimeSlot slot : allCandidateSlots) {
      if (slot.getCandidateId().equals(candidateId)) {
        candidateTimeSlots.add(slot);
      }
    }
    return candidateTimeSlots;
  }

  /**
   * Get list of bookings by candidate slot.
   *
   * @param slotId candidate slot id
   * @return list of bookings
   */
  public List<Booking> getBookingsByCandidateSlotId(UUID slotId) {
    Optional<CandidateTimeSlot> slot = candidateTimeSlotRepository.findById(slotId);
    if (slot.isEmpty()) {
      throw new ResourceNotFoundException("CandidateTimeSlot", "Id", slotId);
    }

    return bookingRepository.getBookingsByCandidateSlotId(slotId);

  }

}
