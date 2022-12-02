package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
  private List<InterviewerBookingLimit> interviewerBookingLimits = new ArrayList<>();
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

    interviewerBookingLimits.add(InterviewerBookingLimit.builder()
        .id(UUID.fromString("3b9c3275-4a2c-4eca-a9e7-be57b5959c79"))
        .weekBookingLimit(5)
        .currentBookingCount(0)
        .interviewerId(UUID.fromString("b73e7eab-1cdb-4cd1-a57b-6d5ec3d8a4ce"))
        .weekNum(WeekUtil.getCurrentWeekNumber()) //Assigned current week
        .build());

  }

  @Test
  void givenValidBookingData_whenCreateBooking_thenReturnBooking() {
    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    Booking booking = bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
        from, to, "Subject1", "Desc1");

    assertThat(booking).isNotNull();

  }

  @Test
  void givenInterviewerWithNoSpaceForNewBooking_whenCreateBooking_thenThrowsInterviewerLimitException() {
    interviewerBookingLimits.get(0).setCurrentBookingCount(5);

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    assertThrows(ValidationException.class,
        () -> bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
            from, to, "Subject1", "Desc1"));
  }

  @Test
  void givenInvalidBookingDuration_whenCreateBooking_thenThrowsValidationException() {
    from = LocalTime.of(10, 0);

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    assertThrows(ValidationException.class,
        () -> bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
            from, to, "Subject1", "Desc1"));
  }

  @Test
  void givenNoBookingLimits_whenCreateBooking_thenBookingCreatedSuccessfully() {
    interviewerBookingLimits.clear();

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    Booking booking = bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
        from, to, "Subject1", "Desc1");

    assertThat(booking).isNotNull();
  }

  @Test
  void givenNextWeekBookingLimit_whenCreateBooking_thenNextWeekLimitSkipped() {

    InterviewerBookingLimit nextWeekLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("3b9c3275-4a2c-4eca-a9e7-be57b5959c79"))
        .weekBookingLimit(5)
        .currentBookingCount(5)
        .interviewerId(UUID.fromString("b73e7eab-1cdb-4cd1-a57b-6d5ec3d8a4ce"))
        .weekNum(WeekUtil.getNextWeekNumber()) //Assigned next week
        .build();

    interviewerBookingLimits.add(nextWeekLimit);

    given(interviewerBookingLimitRepository.findByInterviewerIdAndWeekNum(
        interviewer.getId(),
        WeekUtil.getNextWeekNumber())).willReturn(nextWeekLimit);

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    Booking booking = bookingService.createBooking(interviewerSlotUUID, candidateSlotUUID,
        from, to, "Subject1", "Desc1");

    assertThat(booking).isNotNull();
  }

  @Test
  void givenUpdatedBookingData_whenUpdateBooking_thenBookingUpdatedSuccessfully() {
    Booking curBooking = new Booking(from, to, interviewerSlotUUID,
        candidateSlotUUID, "Subject1", "Desc1");

    from = LocalTime.of(14, 00);
    to = LocalTime.of(15, 30);

    Booking updatedBooking = new Booking(from, to, interviewerSlotUUID,
        candidateSlotUUID, "Subject2", "Desc2");

    given(bookingRepository.findById(curBooking.getId()))
        .willReturn(Optional.of(curBooking));

    given(interviewerTimeSlotRepository.findById(interviewerSlotUUID))
        .willReturn(Optional.of(interviewerTimeSlot));

    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewerTimeSlot.getInterviewerId()))
        .willReturn(interviewerBookingLimits);

    given(candidateTimeSlotRepository.existsById(candidateSlotUUID))
        .willReturn(true);

    bookingService.updateBooking(curBooking.getId(), updatedBooking);

    assertEquals(updatedBooking.getSubject(), curBooking.getSubject());
    assertEquals(updatedBooking.getDescription(), curBooking.getDescription());
    assertEquals(updatedBooking.getInterviewerTimeSlotId(), curBooking.getInterviewerTimeSlotId());
    assertEquals(updatedBooking.getCandidateTimeSlotId(), curBooking.getCandidateTimeSlotId());
    assertEquals(updatedBooking.getFrom(), curBooking.getFrom());
    assertEquals(updatedBooking.getTo(), curBooking.getTo());
  }

  @Test
  void givenNonExistingBookingId_whenUpdateBooking_thenThrowsException() {

    Booking booking = Booking.builder()
        .from(from)
        .to(to)
        .candidateTimeSlotId(candidateSlotUUID)
        .interviewerTimeSlotId(interviewerSlotUUID)
        .subject("subject")
        .description("description")
        .build();

    assertThrows(NotFoundException.class,
        () -> bookingService.updateBooking(UUID.randomUUID(), booking));

    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void givenBookingId_whenDeleteBooking_thenNothing() {

    Booking booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(LocalTime.of(9, 30))
        .to(LocalTime.of(11, 0))
        .subject("Subject")
        .description("Description")
        .build();
    given(bookingRepository.findById(booking.getId())).willReturn(
        Optional.of(booking));

    willDoNothing().given(bookingRepository).delete(booking);

    bookingService.deleteBooking(booking.getId());

    verify(bookingRepository, times(1)).delete(booking);
  }

  @Test
  void givenNonExistingBookingId_whenDeleteBooking_thenThrowsException() {

    Booking booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(LocalTime.of(9, 30))
        .to(LocalTime.of(11, 0))
        .subject("Subject")
        .description("Description")
        .build();

    given(bookingRepository.findById(booking.getId())).willReturn(
        Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.deleteBooking(booking.getId()));

    verify(bookingRepository, times(0)).delete(booking);
  }
}
