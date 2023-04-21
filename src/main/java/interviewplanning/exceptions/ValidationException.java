package interviewplanning.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Validation exception class.
 */
@Getter
@Setter
public class ValidationException extends AbstractCommonException {

  // Slot related validation
  public static final Detail DATE_IS_OUTDATED =
      new Detail(400, "invalid_date", "This date is outdated");
  public static final Detail START_TIME_BIGGER_THAN_END_TIME =
      new Detail(400, "invalid_start_time", "Start time should be less than end time");
  public static final Detail SLOT_BOUNDARIES_NOT_ROUNDED =
      new Detail(400, "invalid_boundaries", "Slot boundaries should be rounded to 30 minutes");
  public static final Detail PERIOD_DURATION_IS_NOT_ENOUGH =
      new Detail(400, "invalid_period", "Period should be more or equal to 1.5h");
  public static final Detail SLOT_BOUNDARIES_EXCEEDED = new Detail(400, "invalid_boundaries",
      "Start time can't be less than 8:00, end time can`t be greater than 22:00");
  public static final Detail NOT_NEXT_WEEK =
      new Detail(400, "invalid_week", "The week must be next to the current");
  public static final Detail NOT_CURRENT_OR_NEXT_WEEK =
      new Detail(400, "invalid_week", "The week number can be for the current or next week only");
  public static final Detail OVERLAPPING_PERIOD =
      new Detail(400, "slot_is_overlapping", "Time slot interval can't overlap existing time slot");
  public static final Detail NOT_WORKING_DAY_OF_WEEK =
      new Detail(400, "invalid_day_of_week", "The day must not be a weekend");

  // Booking related validation
  public static final Detail INTERVIEWER_BOOKING_LIMIT_EXCEEDED =
      new Detail(400, "exceeded_limit", "Interviewer booking limit for this week is exceeded");
  public static final Detail BOOKING_ALREADY_MADE =
      new Detail(400, "cannot_edit_this_slot", "Booking is already made for this slot");
  public static final Detail NOT_NEXT_WEEK_NUMBER =
      new Detail(400, "invalid_week_number", "Provided week number is not the next week number");
  public static final Detail WRONG_BOOKING_DURATION =
      new Detail(400, "invalid_period", "Booking duration must equal to 1.5h");

  public static final Detail BOOKING_OUT_OF_BOUNDS_CANDIDATE =
      new Detail(400, "booking_out_of_bounds_candidate",
          "Booking must be within the time limits of the candidate's slot");

  public static final Detail BOOKING_OUT_OF_BOUNDS_INTERVIEWER =
      new Detail(400, "booking_out_of_bounds_interviewer",
          "Booking must be within the time limits of the interviewer's slot");

  public static final Detail CANDIDATE_SLOT_BOOKED =
      new Detail(400, "candidate_slot_booked", "Candidate time slot is already booked");

  public static final Detail DIFFERENT_SLOTS_DATES =
      new Detail(400, "different_slots_dates",
          "Booking most contain interviewer and candidate slot of same date");

  public static final Detail BOOKING_OVERLAP =
      new Detail(400, "booking_overlap", "Booking's time overlaps with existing one");

  // User related validation
  public static final Detail USER_EMAIL_EXISTS =
      new Detail(400, "invalid_email", "User with this email already exists");

  public static final Detail COORDINATOR_CAN_NOT_BE_REVOKED =
      new Detail(400, "cannot_revoke_this_coordinator", "Coordinator can't revoke himself");

  public ValidationException(Detail detail) {
    super(detail);
  }

  public ValidationException(int statusCode, String errorCode, String errorMessage) {
    super(statusCode, errorCode, errorMessage);
  }
}
