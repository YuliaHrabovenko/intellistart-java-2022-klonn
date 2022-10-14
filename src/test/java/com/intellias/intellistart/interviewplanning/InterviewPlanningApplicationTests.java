package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.time.DayOfWeek;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InterviewPlanningApplicationTests {


  @Autowired
  private InterviewerService interviewerService;

  @Autowired
  private BookingService bookingService;

//  @Test
//  void interviewerSlotMainScenario() {
//    InterviewerTimeSlot timeSlot = new InterviewerTimeSlot(
//        DayOfWeek.FRIDAY,
//        UUID.randomUUID(),
//        UUID.randomUUID()
//    );
//    var slot = interviewerService.createSlot(timeSlot);
//    assertThat(slot).isNotNull();
//  }


  @Test
  void getBooking() {
    var booking = bookingService.getBookingById(UUID.randomUUID());
    assertThat(booking).isNotNull();
  }
}
