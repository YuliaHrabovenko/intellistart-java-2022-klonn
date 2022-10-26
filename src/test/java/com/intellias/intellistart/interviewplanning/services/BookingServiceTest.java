package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerBookingLimitExceededException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import java.time.DayOfWeek;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  @Mock
  private InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  @Mock
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  @InjectMocks
  private BookingService bookingService;

  private LocalTime from;
  private LocalTime to;
  private LocalDate date;
  private User interviewer;
  private User candidate;
  private InterviewerTimeSlot interviewerTimeSlot;
  private CandidateTimeSlot candidateTimeSlot;
  private InterviewerBookingLimit interviewerBookingLimit;
  private UUID interviewerSlotUUID = UUID.fromString("34da35e0-59e4-44bd-a0d0-b0988b93ac88");
  private UUID candidateSlotUUID = UUID.fromString("34da35e0-59e4-44bd-a0d0-b0988b93ac88");

  @BeforeEach
  public void setup() {

    from = LocalTime.of(15, 0);
    to = LocalTime.of(16, 30);
    date = LocalDate.now();

    interviewer = User.builder()
        .id(UUID.fromString("b73e7eab-1cdb-4cd1-a57b-6d5ec3d8a4ce"))
        .email("interviewer@lol.com")
        .role(UserRole.INTERVIEWER)
        .build();

    candidate = User.builder()
        .id(UUID.fromString("bd51c9c6-6e5e-48e7-889f-500b1fc18187"))
        .email("candidate@lol.com")
        .build();

    interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(interviewerSlotUUID)
        .interviewerId(UUID.fromString("b73e7eab-1cdb-4cd1-a57b-6d5ec3d8a4ce"))
        .dayOfWeek(DayOfWeek.MONDAY)
        .from(from)
        .to(to)
        .build();

    candidateTimeSlot = candidateTimeSlot.builder()
        .id(candidateSlotUUID)
        .date(date)
        .from(from)
        .to(to)
        .build();

    interviewerBookingLimit = interviewerBookingLimit.builder()
        .id(UUID.fromString("3b9c3275-4a2c-4eca-a9e7-be57b5959c79"))
        .weekBookingLimit(5)
        .currentBookingCount(0)
        .interviewerId(UUID.fromString("b73e7eab-1cdb-4cd1-a57b-6d5ec3d8a4ce"))
        .build();

  }

  @Test
  void createBooking_allDataValid() {
      given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
          .willReturn(Optional.of(interviewerTimeSlot));

      given(interviewerBookingLimitRepository.findByInterviewerId(interviewerTimeSlot.getInterviewerId()))
          .willReturn(Optional.of(interviewerBookingLimit));

      given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
          .willReturn(true);

      Booking booking = bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
          from, to, "Subject1", "Desc1");

      assertThat(booking).isNotNull();

  }

  @Test
  void validateBooking_interviewerLimitException(){
    interviewerBookingLimit.setCurrentBookingCount(5);

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(interviewerTimeSlot.getInterviewerId()))
        .willReturn(Optional.of(interviewerBookingLimit));

    assertThrows(InterviewerBookingLimitExceededException.class,
        () -> bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
            from, to, "Subject1", "Desc1"));
  }

  @Test
  void validateBooking_invalidDuration() {
    from = LocalTime.of(10, 0);

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(interviewerTimeSlot.getInterviewerId()))
        .willReturn(Optional.of(interviewerBookingLimit));

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    assertThrows(InvalidPeriodException.class,
        () -> bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
            from, to, "Subject1", "Desc1"));
  }

  @Test
  void updateBooking_allDataValid(){
    Booking curBooking = new Booking(from, to, interviewerSlotUUID,
        candidateSlotUUID, "Subject1", "Desc1");

    UUID id = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

    curBooking.setId(id);

    from = LocalTime.of(14, 00);
    to = LocalTime.of(15, 30);

    Booking updatedBooking = new Booking(from, to, interviewerSlotUUID,
        candidateSlotUUID, "Subject2", "Desc2");

    updatedBooking.setId(id);

    given(bookingRepository.findById(curBooking.getId()))
        .willReturn(Optional.of(curBooking));

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(interviewerTimeSlot.getInterviewerId()))
        .willReturn(Optional.of(interviewerBookingLimit));

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    bookingService.updateBooking(curBooking.getId(), updatedBooking);

    assertEquals(updatedBooking, curBooking);
  }
}