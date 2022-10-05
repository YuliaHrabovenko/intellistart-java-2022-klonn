package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.repositories.CandidateRepository;
import org.springframework.stereotype.Service;

/**
 * CandidateTimeSlot business logic.
 */
@Service
public class CandidateTimeSlotService {
  private final CandidateRepository candidateRepository;

  public CandidateTimeSlotService(CandidateRepository candidateRepository) {
    this.candidateRepository = candidateRepository;
  }
}
