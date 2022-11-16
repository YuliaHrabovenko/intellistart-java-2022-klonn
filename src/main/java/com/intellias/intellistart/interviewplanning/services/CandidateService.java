package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.PeriodUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Candidate business logic.
 */
@Service
public class CandidateService {
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   *
   * @param candidateTimeSlotRepository candidate time slot repository
   * @param bookingRepository           booking repository
   */
  @Autowired
  public CandidateService(CandidateTimeSlotRepository candidateTimeSlotRepository,
                          BookingRepository bookingRepository) {
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
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

    PeriodUtil.validatePeriod(from, to);
    PeriodUtil.validateDate(date);

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
            () -> new NotFoundException(ExceptionMessage.CANDIDATE_SLOT_NOT_FOUND.getMessage())
        );

    // Check if there is no bookings with this candidate slot
    List<Booking> bookings =
        bookingRepository.findByCandidateTimeSlotId(existingSlot.getId());

    if (!bookings.isEmpty()) {
      throw new ValidationException(ExceptionMessage.BOOKING_ALREADY_MADE.getMessage());
    }

    LocalTime from = candidateTimeSlot.getFrom();
    LocalTime to = candidateTimeSlot.getTo();

    LocalDate date = candidateTimeSlot.getDate();

    PeriodUtil.validatePeriod(from, to);
    PeriodUtil.validateDate(date);

    existingSlot.setFrom(candidateTimeSlot.getFrom());
    existingSlot.setTo(candidateTimeSlot.getTo());
    existingSlot.setDate(candidateTimeSlot.getDate());

    return candidateTimeSlotRepository.save(existingSlot);
  }

  /**
   * List slots of candidate.
   *
   * @param email email;
   * @return list of candidate`s slots
   */
  public List<CandidateTimeSlot> getSlotsByCandidateEmail(String email) {
    return candidateTimeSlotRepository.findByEmail(email);
  }

}
