package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import org.springframework.stereotype.Service;

/**
 * Booking business logic.
 */
@Service
public class BookingService {
  private final BookingRepository bookingRepository;

  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  public Booking createBooking(Booking booking) {
    return new Booking();
  }

  public Booking getBookingById(Long id) {
    return new Booking();
  }

  public void updateBooking(Long id) {

  }

  public void deleteBooking(Long id) {
    this.bookingRepository.deleteById(id);
  }
}
