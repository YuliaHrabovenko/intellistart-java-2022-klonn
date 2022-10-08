package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Candidate business logic.
 */
@Service
public class CandidateService {
  private final UserRepository candidateRepository;


  public CandidateService(UserRepository candidateRepository) {
    this.candidateRepository = candidateRepository;
  }

  public CandidateTimeSlot createSlot(CandidateTimeSlot candidateTimeSlot) {
    return new CandidateTimeSlot();
  }
}
