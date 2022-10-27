package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService.DayInfo;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coordinator controller.
 */
@RestController

public class CoordinatorController {

  private final CoordinatorService coordinatorService;

  private final BookingService bookingService;

  @Autowired
  public CoordinatorController(CoordinatorService coordinatorService,
                               BookingService bookingService) {
    this.coordinatorService = coordinatorService;
    this.bookingService = bookingService;
  }

  @DeleteMapping(path = "/bookings/{bookingId}")
  public void deleteBooking(@PathVariable("bookingId") UUID bookingId) {
    bookingService.deleteBooking(bookingId);
  }

  //  @PostMapping(path = "/bookings/{bookingId}")
  //  public void updateBooking(@PathVariable("bookingId") UUID bookingId,
  //                            @RequestBody Booking booking){
  //    bookingService.updateBooking(booking, bookingId);
  //  }

  @PostMapping(path = "/bookings")
  @ResponseStatus(code = HttpStatus.CREATED)
  public void createBooking(@RequestBody Booking booking) {
    bookingService.createBooking(booking);
  }

  // all bottom methods are tested and it`s working correctly
  @GetMapping(path = "/users/coordinators")
  public List<User> getCoordinators() {
    return coordinatorService.getCoordinators();
  }

  @GetMapping(path = "/users/interviewers")
  public List<User> getInterviewers() {
    return coordinatorService.getInterviewers();
  }

  @DeleteMapping(path = "/users/coordinators/{coordinatorId}")
  public void revokeCoordinatorRole(@PathVariable("coordinatorId") UUID coordinatorId) {
    coordinatorService.revokeCoordinatorRole(coordinatorId);
  }

  @DeleteMapping(path = "/users/interviewers/{interviewerId}")
  public void revokeInterviewerRole(@PathVariable("interviewerId") UUID interviewerId) {
    coordinatorService.revokeInterviewerRole(interviewerId);
  }

  @PostMapping(path = "/users/coordinators")
  @ResponseStatus(code = HttpStatus.CREATED)
  public void grantCoordinatorRole(@RequestBody User coordinator) {
    coordinatorService.grantCoordinatorRole(coordinator);
  }

  @PostMapping(path = "/users/interviewers")
  @ResponseStatus(code = HttpStatus.CREATED)
  public void grantInterviewerRole(@RequestBody User interviewer) {
    coordinatorService.grantInterviewerRole(interviewer);
  }

  @GetMapping(path = "/weeks/{weekNum}/dashboard")
  public Map<String, DayInfo[]> getDashboard(@PathVariable("weekNum") String weekNum) {
    return coordinatorService.getAllSlotsAndBookingsGroupedByDay(weekNum);
  }

}