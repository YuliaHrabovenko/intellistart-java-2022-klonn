package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import org.springframework.stereotype.Service;

/**
 * Interviewer business logic.
 */
@Service
public class InterviewerService {
  private final InterviewerRepository interviewerRepository;

  public InterviewerService(InterviewerRepository interviewerRepository) {
    this.interviewerRepository = interviewerRepository;
  }

  public InterviewerRepository getInterviewerRepository() {
    return interviewerRepository;
  }

  public InterviewerTimeSlot createSlot(InterviewerTimeSlot timeSlot) {
    return new InterviewerTimeSlot();
  }


}
