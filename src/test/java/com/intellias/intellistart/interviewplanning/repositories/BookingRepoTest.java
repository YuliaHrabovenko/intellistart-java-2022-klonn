package com.intellias.intellistart.interviewplanning.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookingRepoTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private BookingRepository bookingRepository;
  private CandidateTimeSlot candidateSlot;
  private InterviewerTimeSlot interviewerSlot;
  private Booking booking;

  @Before
  public void setup() {
    candidateSlot = CandidateTimeSlot.builder()
        .date(LocalDate.now().plusWeeks(1L))
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(10, 30))
        .email("candidate@gmail.com")
        .build();
    entityManager.persistAndFlush(candidateSlot);
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persistAndFlush(interviewer);
    interviewerSlot = InterviewerTimeSlot.builder()
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .build();
    entityManager.persistAndFlush(interviewerSlot);
    booking = Booking.builder()
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(10, 30))
        .candidateTimeSlotId(candidateSlot.getId())
        .interviewerTimeSlotId(interviewerSlot.getId())
        .subject("subject")
        .description("description")
        .build();
    entityManager.persistAndFlush(booking);
  }

  @Test
  public void shouldFindBookingsByCandidateId() {
    List<Booking> bookings = bookingRepository.findByCandidateTimeSlotId(candidateSlot.getId());
    assertThat(bookings).hasSize(1).contains(booking);
  }

  @Test
  public void shouldFindBookingsByInterviewerId() {
    List<Booking> bookings = bookingRepository.findByInterviewerTimeSlotId(interviewerSlot.getId());
    assertThat(bookings).hasSize(1).contains(booking);
  }
}
