package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;

/**
 * InterviewerBookingLimit business logic.
 */
public class InterviewerBookingLimitService {
  private final InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  public InterviewerBookingLimitService(
      InterviewerBookingLimitRepository interviewerBookingLimitRepository) {
    this.interviewerBookingLimitRepository = interviewerBookingLimitRepository;
  }

}
