package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * CoordinatorService business logic.
 */
@Service
public class CoordinatorService {
  private final UserRepository coordinatorRepository;

  public CoordinatorService(UserRepository coordinatorRepository) {
    this.coordinatorRepository = coordinatorRepository;
  }

  public Booking createBooking(Booking booking) {
    return new Booking();
  }
}
