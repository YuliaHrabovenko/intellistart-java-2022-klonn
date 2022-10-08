package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Interviewer business logic.
 */
@Service
public class InterviewerService {
  private final UserRepository interviewerRepository;

  public InterviewerService(UserRepository interviewerRepository) {
    this.interviewerRepository = interviewerRepository;
  }

  public UserRepository getInterviewerRepository() {
    return interviewerRepository;
  }

  public InterviewerTimeSlot createSlot(InterviewerTimeSlot timeSlot) {
    return new InterviewerTimeSlot();
  }


}
