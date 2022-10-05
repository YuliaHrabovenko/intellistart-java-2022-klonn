package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import org.springframework.stereotype.Service;

/**
 * InterviewerTimeSlot business logic.
 */
@Service
public class InterviewerTimeSlotService {
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  public InterviewerTimeSlotService(InterviewerTimeSlotRepository interviewerTimeSlotRepository) {
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
  }

  public InterviewerTimeSlotRepository getInterviewerTimeSlotRepository() {
    return this.interviewerTimeSlotRepository;
  }
}
