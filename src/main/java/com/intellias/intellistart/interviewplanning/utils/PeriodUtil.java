package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Util class to validate period.
 */
public final class PeriodUtil {
  private static final LocalTime minStartTime = LocalTime.of(8, 0);
  private static final LocalTime maxEndTime = LocalTime.of(22, 0);

  private PeriodUtil() {
  }

  /**
   * Validate slot period.
   *
   * @param from start time
   * @param to   end time
   */
  public static void validatePeriod(LocalTime from, LocalTime to) {
    if (from.isBefore(minStartTime) || to.isAfter(maxEndTime)) {
      throw new ValidationException(ValidationException.SLOT_BOUNDARIES_EXCEEDED);
    }

    if (to.isBefore(from)) {
      throw new ValidationException(ValidationException.START_TIME_BIGGER_THAN_END_TIME);
    }

    if (Math.abs(Duration.between(from, to).toMinutes()) < 90) {
      throw new ValidationException(ValidationException.PERIOD_DURATION_IS_NOT_ENOUGH);
    }

    if (from.getMinute() % 30 != 0 || to.getMinute() % 30 != 0) {
      throw new ValidationException(ValidationException.SLOT_BOUNDARIES_NOT_ROUNDED);
    }
  }

  /**
   * Validate candidate slot date.
   *
   * @param date slot date
   */
  public static void validateDate(LocalDate date) {
    if (date.isBefore(LocalDate.now())) {
      throw new ValidationException(ValidationException.DATE_IS_OUTDATED);
    }
  }

  /**
   * Check if new time slot is not overlapping existing slots.
   *
   * @param existingSlot existing slot
   * @param newSlot      new slot
   */
  public static void isOverlapping(InterviewerTimeSlot existingSlot, InterviewerTimeSlot newSlot) {
    boolean overlaps = (
        (existingSlot.getFrom().isBefore(newSlot.getTo()))
            && (existingSlot.getTo().isAfter(newSlot.getFrom()))
    );
    if (overlaps) {
      throw new ValidationException(ValidationException.OVERLAPPING_PERIOD);
    }
  }

  /**
   * Check if new time slot is not overlapping existing slots.
   *
   * @param existingSlots existing slots
   * @param newSlot       new slot
   */
  public static void isCandidateSlotOverlapping(CandidateTimeSlot newSlot,
                                                List<CandidateTimeSlot> existingSlots) {

    for (CandidateTimeSlot slot : existingSlots) {
      if ((slot.getFrom().isBefore(newSlot.getTo())) && (slot.getTo().isAfter(newSlot.getFrom()))) {
        throw new ValidationException(ValidationException.OVERLAPPING_PERIOD);
      }
    }
  }
}
