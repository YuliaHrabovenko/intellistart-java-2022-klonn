package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * CandidateTimeSlot business logic.
 */
@Service
public class CandidateTimeSlotService {
  private final UserRepository candidateRepository;

  public CandidateTimeSlotService(UserRepository candidateRepository) {
    this.candidateRepository = candidateRepository;
  }
}
