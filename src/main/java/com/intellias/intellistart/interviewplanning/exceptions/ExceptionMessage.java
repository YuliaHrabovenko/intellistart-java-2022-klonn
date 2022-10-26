package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Enum for exception messages.
 */
public enum ExceptionMessage {
  // NOT FOUND exception error messages
  CANDIDATE_SLOT_NOT_FOUND("Candidate time slot with this ID doesn't exist"),
  INTERVIEWER_SLOT_NOT_FOUND("Interviewer time slot with this ID doesn't exist"),
  INTERVIEWER_NOT_FOUND("Interviewer with this ID doesn't exist"),
  BOOKING_NOT_FOUND("Booking with this ID doesn't exist"),

  // VALIDATION exception error messages
  // Slot related validation messages
  DATE_IS_OUTDATED("This date is outdated"),
  START_TIME_BIGGER_THAN_END_TIME("Start time should be less than end time"),
  SLOT_BOUNDARIES_NOT_ROUNDED("Slot boundaries should be rounded to 30 minutes"),
  PERIOD_DURATION_IS_NOT_ENOUGH("Period should be more or equal to 1.5h"),
  INTERVIEWER_SLOT_BOUNDARIES_EXCEEDED(
      "Start time can`t be less than 8:00, end time can`t be greater than 22:00"),
  NOT_WORKING_DAY_OF_WEEK("The day must not be a weekend"),

  // Booking related validation messages
  INTERVIEWER_BOOKING_LIMIT_EXCEEDED("Interviewer booking limit for this week is exceeded"),
  BOOKING_ALREADY_MADE("Booking is already made for this slot"),
  NOT_NEXT_WEEK_NUMBER("Provided week number is not the next week number"),
  WRONG_BOOKING_DURATION("Booking duration must equal to 1.5h"),

  // User info related validation messages
  USER_EMAIL_EXISTS("User with this email already exists");

  private String message;

  ExceptionMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
