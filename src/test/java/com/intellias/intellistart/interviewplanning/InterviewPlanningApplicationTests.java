package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InterviewPlanningApplicationTests {

  @Test
  void contextLoads() {

  }

  @Autowired
  private InterviewerService interviewerService;
  @Autowired
  private CoordinatorService coordinatorService;

  @Autowired
  private CandidateService candidateService;

  @Autowired
  private BookingService bookingService;

  @Test
  void interviewerSlotMainScenario() {
    InterviewerTimeSlot timeSlot = new InterviewerTimeSlot(
        1L,
        DayOfWeek.FRIDAY,
        LocalTime.of(8, 0),
        LocalTime.of(9, 30)
    );
    var slot = interviewerService.createSlot(timeSlot);
    assertThat(slot).isNotNull();
  }

  @Test
  void coordinatorBookingMainScenario() {
    Booking booking = new Booking(
        new InterviewerTimeSlot(),
        new CandidateTimeSlot(),
        LocalTime.of(8, 0),
        LocalTime.of(9, 30),
        "Subject",
        "Description"
    );
    var booking2 = coordinatorService.createBooking(booking);
    assertThat(booking2).isNotNull();
  }

  @Test
  void candidateSlotMainScenario() {
    CandidateTimeSlot candidateTimeSlot = new CandidateTimeSlot(
        1L,
        LocalDateTime.now(),
        LocalTime.of(1, 30)
    );
    var slot = candidateService.createSlot(candidateTimeSlot);
    assertThat(slot).isNotNull();
  }

  @Test
  void getBooking() {
    var booking = bookingService.getBookingById(1L);
    assertThat(booking).isNotNull();
  }
}
