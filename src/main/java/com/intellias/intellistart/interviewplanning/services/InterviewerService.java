package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidMaximumBookingCountException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
   * @param interviewerRepository         interviewer repository
   * @param interviewerTimeSlotRepository interviewer time slot repository
   * @param bookingRepository           booking repository
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
   * @param day   period day
   * @param from  period start time
   * @param to  period end time
   */

  public void validateInterviewerPeriod(DayOfWeek day, LocalTime from, LocalTime to) {

//    Long currentWeekNumber = getCurrentWeekNumber();
//    Long periodWeekNumber = getWeekForSpecificTime(from);

//    if (periodWeekNumber != currentWeekNumber + 1) {
//      throw new InvalidPeriodException(
//          "Period for interviewer`s slot must be for the next week, not current");
//    }

    if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
      throw new InvalidPeriodException(
          "Period for interviewer`s slot must be rounded to 30 minutes");
    }

//    if (from.isBefore(LocalDateTime.now()) || to.isBefore(LocalDateTime.now())) {
//      throw new InvalidPeriodException(
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
//      throw new InvalidPeriodException(
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
   * @param interviewerTimeSlot   interviewer time slot object
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

    validateInterviewerPeriod(interviewerTimeSlot.getDayOfWeek(),
        interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  /**
   * get time slots for current or next week, boolean field indicates
   * for which week get slots, true - current, false - next.
   *
   * @param interviewerId id of interviewer
   * @param isForCurrentWeek field to indicate for which week return slots
   * @return week time slots
   */

  //To avoid code duplication used isForCurrentWeek field,
  // true means yes, for current, false means for the next week
  public List<InterviewerTimeSlot> getWeekTimeSlotsByInterviewerId(
      UUID interviewerId, boolean isForCurrentWeek) {

    Long requiredWeekNumber = isForCurrentWeek
        ? getCurrentWeekNumber() : getCurrentWeekNumber() + 1;

    Optional<User> interviewer = interviewerRepository.findById(interviewerId);
    if (interviewer.isEmpty()) {
      throw new ResourceNotFoundException("Interviewer", "Id", interviewer);
    }

    List<InterviewerTimeSlot> allInterviewerSlots = interviewerTimeSlotRepository.findAll();
    List<InterviewerTimeSlot> interviewerTimeSlots = new ArrayList<>();

    for (InterviewerTimeSlot slot : allInterviewerSlots) {
      if (slot.getInterviewerId().equals(interviewerId)
          && getWeekForSpecificTime(LocalDateTime.from(slot.getFrom())).equals(requiredWeekNumber)) {
        interviewerTimeSlots.add(slot);
      }

    }
    return interviewerTimeSlots;
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
      throw new ResourceNotFoundException("InterviewerTimeSlot", "Id", slotId);
    }
    return bookingRepository.getBookingsByInterviewerSlotId(slotId);
  }

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
//      throw new InvalidMaximumBookingCountException("Maximum booking count must be positive digit");
//    }
//
//    return interviewerBookingLimitRepository.save(interviewerBookingLimit);
//  }

  /**
   * Method to get current week number.
   *
   * @return current week number
   */
  private Long getCurrentWeekNumber() {
    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    LocalDate currentLocalDate = LocalDateTime.now().toLocalDate();
    return (long) currentLocalDate.get(weekFields.weekOfWeekBasedYear());
  }

  /**
   * Method to get week number for specific time.
   *
   * @param time time to which to get week number
   * @return week number for specific time
   */
  private Long getWeekForSpecificTime(LocalDateTime time) {
    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    LocalDate timeLocalDate = time.toLocalDate();
    return (long) timeLocalDate.get(weekFields.weekOfWeekBasedYear());
  }

}
