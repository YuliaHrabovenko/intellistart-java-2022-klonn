package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.ExceptionMessage;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
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

  /**
   * Constructor.
   *
   * @param interviewerRepository             interviewer repository
   * @param interviewerTimeSlotRepository     interviewer time slot repository
   * @param bookingRepository                 booking repository
   * @param interviewerBookingLimitRepository interviewer booking limit repository
   */

  @Autowired
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
      throw new NotFoundException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage());
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
    interviewerTimeSlot.setInterviewerId(interviewerId);

    WeekUtil.validateDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    WeekUtil.validateIsNextWeekNumber(interviewerTimeSlot.getWeekNum(),
        WeekUtil.getNextWeekNumber());
    PeriodUtil.validatePeriod(interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    // check if new time slot is not overlapping existing slots of the day
    List<InterviewerTimeSlot> slots =
        interviewerTimeSlotRepository.findByDayOfWeekAndInterviewerIdAndWeekNum(
            interviewerTimeSlot.getDayOfWeek(),
            interviewerTimeSlot.getInterviewerId(), interviewerTimeSlot.getWeekNum());

    for (InterviewerTimeSlot timeSlot : slots) {
      PeriodUtil.isOverlapping(timeSlot, interviewerTimeSlot);
    }

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  /**
   * Update Interviewer time slot not only for next week.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @param interviewerId       interviewer id
   * @param slotId              slot id
   * @return updated interviewer time slot if valid
   */
  public InterviewerTimeSlot updateSlot(InterviewerTimeSlot interviewerTimeSlot,
                                        UUID interviewerId,
                                        UUID slotId) {
    // check if time is before weekends
    WeekUtil.validateDayOfWeek(LocalDate.now().getDayOfWeek());
    validateInterviewerExistsById(interviewerId);
    interviewerTimeSlot.setInterviewerId(interviewerId);

    InterviewerTimeSlot existingSlot = interviewerTimeSlotRepository.findById(slotId).orElseThrow(
        () -> new NotFoundException(
            ExceptionMessage.INTERVIEWER_SLOT_NOT_FOUND.getMessage()));

    //validation new data
    WeekUtil.validateDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    PeriodUtil.validatePeriod(interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

    // check if there is no bookings with interviewer slot
    List<Booking> bookings = bookingRepository.getBookingsByInterviewerSlotId(existingSlot.getId());

    if (!bookings.isEmpty()) {
      throw new ValidationException(ExceptionMessage.BOOKING_ALREADY_MADE.getMessage());
    }
    // creating a list of slots interviewer has for current day
    List<InterviewerTimeSlot> slots =
        interviewerTimeSlotRepository.findByDayOfWeekAndInterviewerIdAndWeekNum(
            interviewerTimeSlot.getDayOfWeek(),
            interviewerTimeSlot.getInterviewerId(), interviewerTimeSlot.getWeekNum());
    //remove slot that will be updating
    slots.remove(existingSlot);

    //setting fields to the slot that will be updating
    existingSlot.setWeekNum(interviewerTimeSlot.getWeekNum());
    existingSlot.setDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    existingSlot.setFrom(interviewerTimeSlot.getFrom());
    existingSlot.setTo(interviewerTimeSlot.getTo());
    existingSlot.setInterviewerId(interviewerId);

    // check if new time slot is not overlapping existing slots of the day
    for (InterviewerTimeSlot timeslot : slots) {
      PeriodUtil.isOverlapping(timeslot, existingSlot);
    }

    return interviewerTimeSlotRepository.save(existingSlot);
  }

  /**
   * Updating interviewer time slot with checking if slot is in next week.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @param interviewerId       interviewer id
   * @param slotId              slot id
   * @return updated interviewer time slot if valid
   */
  public InterviewerTimeSlot updateSlotForNextWeek(InterviewerTimeSlot interviewerTimeSlot,
                                                   UUID interviewerId,
                                                   UUID slotId) {
    WeekUtil.validateIsNextWeekNumber(interviewerTimeSlot.getWeekNum(),
        WeekUtil.getNextWeekNumber());
    return updateSlot(interviewerTimeSlot, interviewerId, slotId);
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
    return interviewerBookingLimitRepository.findByInterviewerId(
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
   * @param interviewerId    id of interviewer
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
      throw new NotFoundException(ExceptionMessage.INTERVIEWER_NOT_FOUND.getMessage());
    }
    return interviewerTimeSlotRepository.findInterviewerTimeSlotsByInterviewerIdAndWeekNum(
            interviewerId, requiredWeekNumber);
  }

}
