package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.utils.PeriodUtil;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Interviewer business logic.
 */
@Service
public class InterviewerService {
  private final UserRepository interviewerRepository;
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final BookingRepository bookingRepository;
  private final InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  /**
   * Constructor.
   *
   * @param interviewerRepository             interviewer repository
   * @param interviewerTimeSlotRepository     interviewer time slot repository
   * @param bookingRepository                 booking repository
   * @param interviewerBookingLimitRepository interviewer booking limit repository
   */

  public InterviewerService(UserRepository interviewerRepository,
                            InterviewerTimeSlotRepository interviewerTimeSlotRepository,
                            BookingRepository bookingRepository,
                            InterviewerBookingLimitRepository interviewerBookingLimitRepository) {
    this.interviewerRepository = interviewerRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.bookingRepository = bookingRepository;
    this.interviewerBookingLimitRepository = interviewerBookingLimitRepository;
  }

  /**
   * Validate if interviewer exists in the db.
   *
   * @param interviewerId interviewer`s id
   */
  public void validateInterviewerExistsById(UUID interviewerId) {
    Optional<User> interviewer = interviewerRepository
        .findById(interviewerId);
    if (interviewer.isEmpty()) {
      throw new ValidationException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage());
    }
  }

  /**
   * Validate if new time slot is not overlapping existing slots.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @param interviewerId       interviewer id
   */
  public void validateTimeOfTimeSlot(InterviewerTimeSlot interviewerTimeSlot, UUID interviewerId) {
    List<InterviewerTimeSlot> slots =
        interviewerTimeSlotRepository.findByInterviewerId(interviewerId);
    if (!slots.isEmpty()) {
      for (InterviewerTimeSlot timeslot : slots) {
        if (interviewerTimeSlot.getFrom().isAfter(timeslot.getFrom())
            && interviewerTimeSlot.getTo().isBefore(timeslot.getTo())
            // new time slot is in the middle
            || interviewerTimeSlot.getTo().isAfter(timeslot.getFrom())
            && interviewerTimeSlot.getTo().isBefore(timeslot.getTo())
            // ending of time slot is inside another one
            || interviewerTimeSlot.getFrom().isAfter(timeslot.getFrom())
            && interviewerTimeSlot.getFrom().isBefore(
            timeslot.getTo())) {// beginning of the time slot is inside another one
          throw new ValidationException(ExceptionMessage.OVERLAPPING_PERIOD.getMessage());
        }
      }
    }
  }

  /**
   * Create interviewer slot.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @return new interviewer time slot if valid
   */
  public InterviewerTimeSlot createSlot(InterviewerTimeSlot interviewerTimeSlot,
                                        UUID interviewerId) {
    // check if time is before weekends
    WeekUtil.validateDayOfWeek(LocalDate.now().getDayOfWeek());
    validateInterviewerExistsById(interviewerId);

    validateTimeOfTimeSlot(interviewerTimeSlot, interviewerId);

    WeekUtil.validateDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    WeekUtil.validateIsNextWeekNumber(interviewerTimeSlot.getWeekNum(),
        WeekUtil.getNextWeekNumber());
    PeriodUtil.validatePeriod(interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    interviewerTimeSlot.setInterviewerId(interviewerId);

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  /**
   * Update Interviewer time slot.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @return updated interviewer time slot if valid
   */
  public InterviewerTimeSlot updateSlot(InterviewerTimeSlot interviewerTimeSlot,
                                        UUID interviewerId,
                                        UUID slotId) {
    // check if time is before weekends
    WeekUtil.validateDayOfWeek(LocalDate.now().getDayOfWeek());
    validateInterviewerExistsById(interviewerId);

    validateTimeOfTimeSlot(interviewerTimeSlot, interviewerId);

    InterviewerTimeSlot existingSlot =
        interviewerTimeSlotRepository.findById(slotId).orElseThrow(
            () -> new ValidationException(
                ExceptionMessage.INTERVIEWER_SLOT_NOT_FOUND.getMessage()));

    // Check if there is no bookings with interviewer slot
    List<Booking> bookings = bookingRepository.getBookingsByInterviewerSlotId(existingSlot.getId());

    if (!bookings.isEmpty()) {
      throw new ValidationException(ExceptionMessage.BOOKING_ALREADY_MADE.getMessage());
    }

    WeekUtil.validateDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    WeekUtil.validateIsNextWeekNumber(interviewerTimeSlot.getWeekNum(),
        WeekUtil.getNextWeekNumber());
    PeriodUtil.validatePeriod(interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    existingSlot.setWeekNum(interviewerTimeSlot.getWeekNum());
    existingSlot.setDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    existingSlot.setFrom(interviewerTimeSlot.getFrom());
    existingSlot.setTo(interviewerTimeSlot.getTo());
    existingSlot.setInterviewerId(interviewerId);

    return interviewerTimeSlotRepository.save(existingSlot);
  }


  /**
   * Get booking limits by interviewer`s id.
   *
   * @param interviewerId interviewer`s id
   * @return list of booking limits
   */
  public List<InterviewerBookingLimit> getBookingLimitsByInterviewerId(UUID interviewerId) {
    // check if interviewer exists in database
    validateInterviewerExistsById(interviewerId);
    return interviewerBookingLimitRepository.findInterviewerBookingLimitsByInterviewerId(
        interviewerId);
  }

  /**
   * Set interviewer`s booking limit for the next week.
   *
   * @param interviewerBookingLimit interviewer booking limit object
   * @return interviewer booking limit object
   */
  public InterviewerBookingLimit setNextWeekInterviewerBookingLimit(
      InterviewerBookingLimit interviewerBookingLimit) {

    // check if interviewer with this id exists in db
    validateInterviewerExistsById(interviewerBookingLimit.getInterviewerId());

    // check if weekNum is for the next week
    String weekNum = interviewerBookingLimit.getWeekNum();
    String nextWeekNumber = WeekUtil.getNextWeekNumber();
    WeekUtil.validateIsNextWeekNumber(weekNum, nextWeekNumber);

    // if the interviewer has already a booking limit for
    // the next week, just update the booking limit
    InterviewerBookingLimit existingBookingLimit =
        interviewerBookingLimitRepository.findInterviewerBookingLimitByInterviewerIdAndWeekNum(
            interviewerBookingLimit.getInterviewerId(), weekNum);

    if (existingBookingLimit != null) {
      // set a new number of a week booking limit for the next week
      existingBookingLimit.setWeekBookingLimit(interviewerBookingLimit.getWeekBookingLimit());
      return interviewerBookingLimitRepository.save(existingBookingLimit);
    }

    return interviewerBookingLimitRepository.save(interviewerBookingLimit);
  }

  //  /**
  //   * get time slots for current or next week.
  //   *
  //   * @param interviewerId id of interviewer
  //   * @param isForCurrentWeek field to indicate for which week return slots
  //   * @return week time slots
  //   */

  //To avoid code duplication used isForCurrentWeek field,
  // true means yes, for current, false means for the next week
  //  public List<InterviewerTimeSlot> getWeekTimeSlotsByInterviewerId(
  //      UUID interviewerId, boolean isForCurrentWeek) {
  //
  //    Long requiredWeekNumber = isForCurrentWeek
  //        ? getCurrentWeekNumber() : getCurrentWeekNumber() + 1;
  //
  //    Optional<User> interviewer = interviewerRepository.findById(interviewerId);
  //    if (interviewer.isEmpty()) {
  //      throw new ResourceNotFoundException("Interviewer", "Id", interviewer);
  //    }
  //
  //    List<InterviewerTimeSlot> allInterviewerSlots = interviewerTimeSlotRepository.findAll();
  //    List<InterviewerTimeSlot> interviewerTimeSlots = new ArrayList<>();
  //
  //    for (InterviewerTimeSlot slot : allInterviewerSlots) {
  //      Optional<Period> period = periodRepository.findById(slot.getPeriodId());
  //      if (period.isEmpty()) {
  //        throw new ResourceNotFoundException("Period", "Id", period);
  //      }
  //
  //      if (slot.getInterviewerId().equals(interviewerId)
  //          && getWeekForSpecificTime(period.get().getFrom()).equals(requiredWeekNumber)) {
  //        interviewerTimeSlots.add(slot);
  //      }
  //
  //    }
  //    return interviewerTimeSlots;
  //  }

  //  /**
  //   * Set maximum booking for next week.
  //   *
  //   * @param interviewerBookingLimit booking limit for next week
  //   * @return interviewer booking limit if valid
  //   */
  //
  //  public InterviewerBookingLimit setMaximumBookingsForNextWeek(
  //      InterviewerBookingLimit interviewerBookingLimit) {
  //    Optional<User> interviewer = interviewerRepository
  //        .findById(interviewerBookingLimit.getInterviewerId());
  //    if (interviewer.isEmpty()) {
  //      throw new ResourceNotFoundException(
  //          "Interviewer", "Id", interviewerBookingLimit.getInterviewerId());
  //    }
  //
  //    if (interviewerBookingLimit.getWeekBookingLimit() < 0) {
  //      throw new InvalidMaximumBookingCountException(
  //      "Maximum booking count must be positive digit");
  //    }
  //
  //    return interviewerBookingLimitRepository.save(interviewerBookingLimit);
  //  }

}
