package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import org.springframework.stereotype.Service;

/**
 * PeriodService business logic.
 */
@Service
public class PeriodService {
  private final PeriodRepository periodRepository;

  public PeriodService(PeriodRepository periodRepository) {
    this.periodRepository = periodRepository;
  }

}
