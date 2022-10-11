package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.BookingStatus;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        DayOfWeek.FRIDAY,
        1L,
        2L
    );
    var slot = interviewerService.createSlot(timeSlot);
    assertThat(slot).isNotNull();
  }

  @Test
  void coordinatorBookingMainScenario() {
    Booking booking = new Booking(
        1L,
        2L,
        3L,
        BookingStatus.BOOKED,
        "Subject",
        "Description"
    );
    var booking2 = coordinatorService.createBooking(booking);
    assertThat(booking2).isNotNull();
  }

  @Test
  void getBooking() {
    var booking = bookingService.getBookingById(1L);
    assertThat(booking).isNotNull();
  }
}
