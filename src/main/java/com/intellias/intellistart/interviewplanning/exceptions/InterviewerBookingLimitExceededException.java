package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception that appears when interviewer's booking limit was exceeded.
 */
public class InterviewerBookingLimitExceededException extends RuntimeException {

  public InterviewerBookingLimitExceededException() {
    super("Interviewer booking limit was exceeded");
  }
}
