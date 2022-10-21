package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidWeekNumberException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.Week;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.repositories.WeekRepository;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final WeekRepository weekRepository;

  @Autowired
  private WeekService weekService;

  /**
   * Constructor.
   *
   * @param interviewerRepository             interviewer repository
   * @param interviewerTimeSlotRepository     interviewer time slot repository
   * @param bookingRepository                 booking repository
   * @param interviewerBookingLimitRepository interviewer booking limit repository
   * @param weekRepository                    week repository
   */

  public InterviewerService(UserRepository interviewerRepository,
                            InterviewerTimeSlotRepository interviewerTimeSlotRepository,
                            BookingRepository bookingRepository,
                            InterviewerBookingLimitRepository interviewerBookingLimitRepository,
                            WeekRepository weekRepository) {
    this.interviewerRepository = interviewerRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.bookingRepository = bookingRepository;
    this.interviewerBookingLimitRepository = interviewerBookingLimitRepository;
    this.weekRepository = weekRepository;
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
      throw new InvalidPeriodException(
          "Period for interviewer`s slot must be rounded to 30 minutes");
    }

    //    if (from.isBefore(LocalTime.now()) || to.isBefore(LocalTime.now())) {
    //      throw new InvalidInterviewerPeriodException(
    //          "Period which starts at " + from + " and ends "
    //              + to + " must be later then current time");
    //    }

    if (to.isBefore(from)) {
      throw new InvalidPeriodException(
          "The beginning " + from + " must be before the end " + to);
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) < 90) {
      throw new InvalidPeriodException(
          "Period for interviewer`s slot must be more or equal to 1.5h");
    }

    if (from.isBefore(LocalTime.of(8, 0))
        || to.isAfter(LocalTime.of(22, 0))) {
      throw new InvalidPeriodException(
          "Start time can`t be less than 8:00, end time can`t be greater than 22:00");
    }

    //    if (from.getDayOfMonth() != to.getDayOfMonth()
    //        || from.getYear() != to.getYear() || !from.getMonth().equals(to.getMonth())) {
    //      throw new InvalidInterviewerPeriodException(
    //          "Both start and end must be at the same day");
    //    }

    if (day.equals(DayOfWeek.SUNDAY) || day.equals(DayOfWeek.SATURDAY)) {
      throw new InvalidPeriodException(
          "The day must not be a weekend");
    }

  }

  /**
   * Create interviewer slot.
   *
   * @param interviewerTimeSlot interviewer time slot object
   * @return new interviewer time slot if valid
   */

  public InterviewerTimeSlot createSlot(InterviewerTimeSlot interviewerTimeSlot) {
    Optional<User> interviewer = interviewerRepository
        .findById(interviewerTimeSlot.getInterviewerId());
    if (interviewer.isEmpty()) {
      throw new ResourceNotFoundException(
          "Candidate", "Id", interviewerTimeSlot.getInterviewerId());
    }

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    DayOfWeek day = interviewerTimeSlot.getDayOfWeek();
    Week week = interviewerTimeSlot.getWeek();

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
      throw new ResourceNotFoundException(
          "InterviewerTimeSlot", "Id", interviewerTimeSlot.getId());
    }

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  public List<InterviewerBookingLimit> getBookingLimitsById(UUID interviewerId) {
    // check if interviewer exists in database

    return interviewerBookingLimitRepository.getInterviewerBookingLimitsByInterviewerId(
        interviewerId);
  }

  public void validateIsNextWeekNumber(String weekNum, String nextWeekNum){
    if (!nextWeekNum.equals(weekNum)) {
      throw new InvalidWeekNumberException(
          "Week number " + weekNum + " is not equal to the next week number " + nextWeekNum);
    }
  }

  public void setWeekId(Week week){
    Week existingWeek = weekRepository.getWeekByWeekNumber(week.getWeekNumber());

    if (existingWeek != null) {
      // if weekNumber is already in database, set its id to interviewerBookingLimit week object
      week.setId(existingWeek.getId());
    } else {
      // create new week object with new weekNum and set its id to the interviewerBookingLimit week object
      Week newWeek = new Week(week.getWeekNumber());
      weekRepository.save(newWeek);
      week.setId(newWeek.getId());
    }
  }

  public InterviewerBookingLimit setNextWeekInterviewerBookingLimit(
      InterviewerBookingLimit interviewerBookingLimit) {

    // check if weekNumber is for the next week
    Week week = interviewerBookingLimit.getWeek();
    String nextWeekNumber = weekService.getNextWeekNumber().getWeekNumber();

    validateIsNextWeekNumber(week.getWeekNumber(), nextWeekNumber);
    setWeekId(week);

    // if the interviewer has already a booking limit for
    // the next week, just update the booking limit
    InterviewerBookingLimit existingBookingLimit =
        interviewerBookingLimitRepository.getBookingLimitByInterviewerIdAndWeekId(
            interviewerBookingLimit.getInterviewerId(),
            weekRepository.getWeekByWeekNumber(nextWeekNumber).getId());

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
