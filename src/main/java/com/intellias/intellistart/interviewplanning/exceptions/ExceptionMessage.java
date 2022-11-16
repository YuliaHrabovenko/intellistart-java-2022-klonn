package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Enum for exception messages.
 */
public enum ExceptionMessage {
  // NOT FOUND exception error messages
  CANDIDATE_SLOT_NOT_FOUND("Candidate time slot with this ID doesn't exist"),
  INTERVIEWER_SLOT_NOT_FOUND("Interviewer time slot with this ID doesn't exist"),
  INTERVIEWER_NOT_FOUND("Interviewer with this ID doesn't exist"),
  COORDINATOR_NOT_FOUND("Coordinator with with ID doesn't exists"),
  BOOKING_NOT_FOUND("Booking with this ID doesn't exist"),
  USER_NOT_FOUND("User with this email doesn't exist"),

  // VALIDATION exception error messages
  // Slot related validation messages
  NOT_VALID_SLOT_DATA("New slot must be in the same week and day"),
  DATE_IS_OUTDATED("This date is outdated"),
  START_TIME_BIGGER_THAN_END_TIME("Start time should be less than end time"),
  SLOT_BOUNDARIES_NOT_ROUNDED("Slot boundaries should be rounded to 30 minutes"),
  PERIOD_DURATION_IS_NOT_ENOUGH("Period should be more or equal to 1.5h"),
  SLOT_BOUNDARIES_EXCEEDED(
      "Start time can`t be less than 8:00, end time can`t be greater than 22:00"),
  NOT_NEXT_WEEK("The week must be next to the current"),
  NOT_CURRENT_OR_NEXT_WEEK("The week number can be for the current or next week only"),
  OVERLAPPING_PERIOD("Time slot interval can't overlap existing time slot"),
  NOT_WORKING_DAY_OF_WEEK("The day must not be a weekend"),

  // Booking related validation messages
  INTERVIEWER_BOOKING_LIMIT_EXCEEDED("Interviewer booking limit for this week is exceeded"),
  BOOKING_ALREADY_MADE("Booking is already made for this slot"),
  NOT_NEXT_WEEK_NUMBER("Provided week number is not the next week number"),
  WRONG_BOOKING_DURATION("Booking duration must equal to 1.5h"),

  // User info related validation messages
  NOT_COORDINATOR("User with this ID is not a coordinator"),
  NOT_INTERVIEWER("User with this ID is not an interviewer"),
  USER_EMAIL_EXISTS("User with this email already exists"),
  COORDINATOR_CAN_NOT_BE_REVOKED("Coordinator can't revoke himself"),

  // Authentication related validation messages
  INVALID_AUTH_TOKEN("Authorization code is invalid"),

  INVALID_JWT_TOKEN("JWT token is expired or invalid"),
  ACCESS_UNAUTHORIZED("Access Unauthorized");



  private String message;

  ExceptionMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}