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
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
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
   * Interviewer time period validation.
   *
   * @param day  period day
   * @param from period start time
   * @param to   period end time
   */

  public void validateInterviewerPeriod(DayOfWeek day, LocalTime from, LocalTime to) {

    //    Long periodWeekNumber = getWeekForSpecificTime(from);

    //    if (periodWeekNumber != currentWeekNumber + 1) {
    //      throw new InvalidInterviewerPeriodException(
    //          "Period for interviewer`s slot must be for the next week, not current");
    //    }

    if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
      throw new ValidationException(ExceptionMessage.SLOT_BOUNDARIES_NOT_ROUNDED.getMessage());
    }

    //    if (from.isBefore(LocalTime.now()) || to.isBefore(LocalTime.now())) {
    //      throw new InvalidInterviewerPeriodException(
    //          "Period which starts at " + from + " and ends "
    //              + to + " must be later then current time");
    //    }

    if (to.isBefore(from)) {
      throw new ValidationException(ExceptionMessage.START_TIME_BIGGER_THAN_END_TIME.getMessage());
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) < 90) {
      throw new ValidationException(ExceptionMessage.PERIOD_DURATION_IS_NOT_ENOUGH.getMessage());
    }

    if (from.isBefore(LocalTime.of(8, 0))
        || to.isAfter(LocalTime.of(22, 0))) {
      throw new ValidationException(
          ExceptionMessage.INTERVIEWER_SLOT_BOUNDARIES_EXCEEDED.getMessage());
    }

    //    if (from.getDayOfMonth() != to.getDayOfMonth()
    //        || from.getYear() != to.getYear() || !from.getMonth().equals(to.getMonth())) {
    //      throw new InvalidInterviewerPeriodException(
    //          "Both start and end must be at the same day");
    //    }

    if (day.equals(DayOfWeek.SUNDAY) || day.equals(DayOfWeek.SATURDAY)) {
      throw new ValidationException(ExceptionMessage.NOT_WORKING_DAY_OF_WEEK.getMessage());
    }

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
   * Create interviewer slot.
   *
   * @param interviewerTimeSlot   interviewer time slot object
   * @return new interviewer time slot if valid
   */

  public InterviewerTimeSlot createSlot(InterviewerTimeSlot interviewerTimeSlot) {
    Optional<User> interviewer = interviewerRepository
        .findById(interviewerTimeSlot.getInterviewerId());
    if (interviewer.isEmpty()) {
      throw new ValidationException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage());
    }

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    DayOfWeek day = interviewerTimeSlot.getDayOfWeek();

    validateInterviewerPeriod(day, from, to);

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  /**
   * Update Interviewer time slot.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @return updated interviewer time slot if valid
   */

  public InterviewerTimeSlot updateSlot(InterviewerTimeSlot interviewerTimeSlot) {

    Optional<InterviewerTimeSlot> slot = interviewerTimeSlotRepository
        .findById(interviewerTimeSlot.getId());
    if (slot.isEmpty()) {
      throw new ValidationException(ExceptionMessage.INTERVIEWER_SLOT_NOT_FOUND.getMessage());
    }

    validateInterviewerPeriod(interviewerTimeSlot.getDayOfWeek(),
        interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
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

  /**
   * get time slots for current or next week.
   *
   * @param interviewerId id of interviewer
   * @param isForCurrentWeek field to indicate for which week return slots
   * @return week time slots
   */

  //To avoid code duplication used isForCurrentWeek field,
  // true means yes, for current, false means for the next week
  public List<InterviewerTimeSlot> getWeekTimeSlotsByInterviewerId(
      UUID interviewerId, boolean isForCurrentWeek) {

    String requiredWeekNumber = isForCurrentWeek
        ? WeekUtil.getCurrentWeekNumber() : WeekUtil.getNextWeekNumber();

    Optional<User> interviewer = interviewerRepository.findById(interviewerId);
    if (interviewer.isEmpty()) {
      throw new ValidationException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage());
    }

    List<InterviewerTimeSlot> interviewerTimeSlots =
        interviewerTimeSlotRepository.findInterviewerTimeSlotByInterviewerId(interviewerId);
    List<InterviewerTimeSlot> resultTimeSlots = new ArrayList<>();

    for (InterviewerTimeSlot slot : interviewerTimeSlots) {
      if (slot.getWeekNum().equals(requiredWeekNumber)) {
        resultTimeSlots.add(slot);
      }

    }
    return resultTimeSlots;
  }

  /**
   * get booking by interviewer slot id.
   *
   * @param slotId slot id
   * @return list of bookings
   */

  public List<Booking> getBookingByInterviewerSlotId(UUID slotId) {
    Optional<InterviewerTimeSlot> slot = interviewerTimeSlotRepository.findById(slotId);
    if (slot.isEmpty()) {
      throw new ValidationException(ExceptionMessage.INTERVIEWER_SLOT_NOT_FOUND.getMessage());
    }
    return bookingRepository.getBookingsByInterviewerSlotId(slotId);
  }

  /*
  /**
  * Set maximum booking for next week.
  *
  * @param interviewerBookingLimit booking limit for next week
  * @return interviewer booking limit if valid
  * /

  public InterviewerBookingLimit setMaximumBookingsForNextWeek(
      InterviewerBookingLimit interviewerBookingLimit) {
    Optional<User> interviewer = interviewerRepository
        .findById(interviewerBookingLimit.getInterviewerId());
    if (interviewer.isEmpty()) {
      throw new ResourceNotFoundException(
          "Interviewer", "Id", interviewerBookingLimit.getInterviewerId());
    }

    if (interviewerBookingLimit.getWeekBookingLimit() < 0) {
      throw new InvalidMaximumBookingCountException(
      "Maximum booking count must be positive digit");
    }

    return interviewerBookingLimitRepository.save(interviewerBookingLimit);
  }
  */

}
