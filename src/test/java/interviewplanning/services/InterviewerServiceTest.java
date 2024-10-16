package interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import interviewplanning.exceptions.NotFoundException;
import interviewplanning.exceptions.ValidationException;
import interviewplanning.models.Booking;
import interviewplanning.models.InterviewerBookingLimit;
import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.models.User;
import interviewplanning.models.UserRole;
import interviewplanning.repositories.BookingRepository;
import interviewplanning.repositories.InterviewerBookingLimitRepository;
import interviewplanning.repositories.InterviewerTimeSlotRepository;
import interviewplanning.repositories.UserRepository;
import interviewplanning.utils.WeekUtil;
import interviewplanning.services.InterviewerService;
import java.time.DayOfWeek;
import java.time.LocalTime;
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
class InterviewerServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  @Mock
  private InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private InterviewerService interviewerService;

  private User interviewer;
  private LocalTime startTime;
  private LocalTime endTime;

  @BeforeEach
  public void setup() {
    interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .email("interviewer@gmail.com")
        .role(UserRole.INTERVIEWER)
        .build();

    startTime = LocalTime.of(15, 30);
    endTime = LocalTime.of(17, 0);
  }

  @Test
  void givenInterviewerTimeSlot_whenCreateInterviewerSlot_thenReturnInterviewerTimeSlot() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    InterviewerTimeSlot savedSlot =
        interviewerService.createSlot(interviewerTimeSlot, interviewer.getId());

    assertThat(savedSlot).isNotNull();

  }

  @Test
  void givenNonExistPeriodIdAndInterviewerId_whenCreateInterviewerSlot_thenThrowsException() {
    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .build();

    assertThrows(NotFoundException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenEndPeriodTimeBiggerThanStartPeriodTime_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(16, 30);
    LocalTime end = LocalTime.of(15, 0);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenUnroundedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(15, 27);
    LocalTime end = LocalTime.of(17, 0);

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreatInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(15, 0);
    LocalTime end = LocalTime.of(16, 0);


    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodBefore8AM_whenCreatInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(7, 0);
    LocalTime end = LocalTime.of(16, 0);

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodAfter10PM_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(10, 0);
    LocalTime end = LocalTime.of(23, 0);


    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodsStartsAtWeekends_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(10, 0);
    LocalTime end = LocalTime.of(23, 0);


    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.SATURDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot, interviewer.getId()));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenInterviewerSlotObjectWithNotValidInterviewerId_whenUpdateInterviewerSlot_thenThrowException() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(startTime)
        .to(endTime)
        .interviewerId(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    given(interviewerTimeSlotRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.empty());

    LocalTime startNew = LocalTime.of(14, 0);
    LocalTime endNew = LocalTime.of(16, 0);

    interviewerTimeSlot.setFrom(startNew);
    interviewerTimeSlot.setTo(endNew);

    assertThrows(NotFoundException.class,
        () -> interviewerService.updateSlotForNextWeek(interviewerTimeSlot, interviewer.getId(),
            UUID.fromString("123e4567-e89b-42d3-a456-556642440002")));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenInterviewerSlotObjectWithNotValidInterviewerTimeSlot_whenUpdateInterviewerSlot_thenThrowException() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(startTime)
        .to(endTime)
        .interviewerId(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerTimeSlotRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.empty());

    LocalTime startNew = LocalTime.of(14, 0);
    LocalTime endNew = LocalTime.of(16, 0);

    interviewerTimeSlot.setFrom(startNew);
    interviewerTimeSlot.setTo(endNew);

    assertThrows(NotFoundException.class,
        () -> interviewerService.updateSlotForNextWeek(interviewerTimeSlot, interviewer.getId(),
            UUID.fromString("123e4567-e89b-42d3-a456-556642440002")));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenInterviewerSlotObject_whenUpdateInterviewerSlot_thenReturnUpdatedInterviewerSlot() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerTimeSlotRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.of(interviewerTimeSlot));
    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);
    LocalTime startNew = LocalTime.of(14, 0);
    LocalTime endNew = LocalTime.of(16, 0);

    interviewerTimeSlot.setFrom(startNew);
    interviewerTimeSlot.setTo(endNew);

    InterviewerTimeSlot updatedSlot =
        interviewerService.updateSlotForNextWeek(interviewerTimeSlot, interviewer.getId(),
            UUID.fromString("123e4567-e89b-42d3-a456-556642440002"));

    assertThat(updatedSlot.getFrom()).isEqualTo(startNew);
    assertThat(updatedSlot.getTo()).isEqualTo(endNew);
  }

  @Test
  void givenBookedInterviewerSlot_whenUpdateInterviewerSlot_thenThrowsException() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerTimeSlotRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.of(interviewerTimeSlot));

    Booking booking = Booking.builder()
        .interviewerTimeSlotId(interviewerTimeSlot.getId())
        .candidateTimeSlotId(UUID.randomUUID())
        .from(startTime)
        .to(endTime)
        .subject("subject")
        .description("description")
        .build();

    given(bookingRepository.findByInterviewerTimeSlotId(interviewerTimeSlot.getId())).willReturn(
        List.of(booking));

    interviewerTimeSlot.setFrom(LocalTime.of(14, 0));
    interviewerTimeSlot.setTo(LocalTime.of(16, 0));

    assertThrows(ValidationException.class,
        () -> interviewerService.updateSlotForNextWeek(interviewerTimeSlot, interviewer.getId(),
            UUID.fromString("123e4567-e89b-42d3-a456-556642440002")));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenOverlappingSlot_whenCreateInterviewerSlot_thenThrowsException() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerTimeSlotRepository.findByDayOfWeekAndInterviewerIdAndWeekNum(DayOfWeek.MONDAY,
        interviewer.getId(), WeekUtil.getNextWeekNumber())).willReturn(List.of(interviewerTimeSlot));

    InterviewerTimeSlot overlappingSlot = InterviewerTimeSlot.builder()
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();
    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

//    assertThrows(ValidationException.class,
//        () -> interviewerService.createSlot(overlappingSlot, interviewer.getId()));

    ValidationException exception = assertThrows(
        ValidationException.class,
        () -> interviewerService.createSlot(overlappingSlot, interviewer.getId()));
    assertEquals("Time slot interval can't overlap existing time slot",
        exception.getErrorMessage());
  }

  @Test
  void givenInterviewerBookingLimit_whenSetMaximumBookingsLimit_thenReturnInterviewerBookingLimit() {
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .weekBookingLimit(3)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerBookingLimitRepository.save(interviewerBookingLimit)).willReturn(
        interviewerBookingLimit);

    InterviewerBookingLimit savedInterviewerBookingLimit =
        interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit,
            interviewer.getId());

    assertThat(savedInterviewerBookingLimit.getWeekBookingLimit()).isEqualTo(3);
    assertThat(savedInterviewerBookingLimit.getWeekNum()).isEqualTo(WeekUtil.getNextWeekNumber());
    assertThat(savedInterviewerBookingLimit.getInterviewerId()).isEqualTo(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
  }

  @Test
  void givenInterviewerBookingLimit_whenChangeMaximumBookingLimit_thenReturnUpdatedInterviewerBookingLimit() {
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .weekBookingLimit(3)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    given(interviewerBookingLimitRepository.save(interviewerBookingLimit)).willReturn(
        interviewerBookingLimit);

    interviewerBookingLimit.setWeekBookingLimit(8);

    given(interviewerBookingLimitRepository.findByInterviewerIdAndWeekNum(
        interviewerBookingLimit.getInterviewerId(),
        interviewerBookingLimit.getWeekNum())).willReturn(interviewerBookingLimit);

    InterviewerBookingLimit updatedInterviewerBookingLimit =
        interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit,
            interviewer.getId());

    assertThat(updatedInterviewerBookingLimit.getId()).isEqualTo(
        interviewerBookingLimit.getInterviewerId());
    assertThat(updatedInterviewerBookingLimit.getWeekBookingLimit()).isEqualTo(8);
  }

  @Test
  void givenInterviewerBookingLimit_whenSetMaximumBookings_thenThrowInterviewerNotFoundException() {
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(UUID.randomUUID())
        .weekBookingLimit(3)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    assertThrows(NotFoundException.class,
        () -> interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit,
            UUID.randomUUID()));

    verify(interviewerBookingLimitRepository, never()).save(any(InterviewerBookingLimit.class));
  }

  @Test
  void givenInterviewerBookingLimit_whenSetMaximumBookings_thenThrowNotNextWeekException() {
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .weekBookingLimit(3)
        .weekNum("201840")
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    assertThrows(ValidationException.class,
        () -> interviewerService.setNextWeekInterviewerBookingLimit(interviewerBookingLimit,
            interviewer.getId()));

    verify(interviewerBookingLimitRepository, never()).save(any(InterviewerBookingLimit.class));
  }

  @Test
  void givenInterviewerId_whenGetInterviewerBookingLimits_thenReturnBookingLimitsList() {
    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerId(interviewer.getId())
        .weekBookingLimit(3)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
    List<InterviewerBookingLimit> expectedLimits = List.of(interviewerBookingLimit);
    given(interviewerBookingLimitRepository.findByInterviewerId(
        interviewer.getId())).willReturn(expectedLimits);

    List<InterviewerBookingLimit> receivedLimits =
        interviewerService.getBookingLimitsByInterviewerId(interviewer.getId());
    assertThat(receivedLimits).isEqualTo(expectedLimits);
  }

  @Test
  void givenInterviewerIdAndIsForCurrentWeekTrue_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {

    User interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .email("interrviewer@gmail.com")
        .role(UserRole.INTERVIEWER)
        .build();

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(startTime)
        .to(endTime)
        .dayOfWeek(DayOfWeek.FRIDAY)
        .interviewerId(interviewer.getId())
        .weekNum(WeekUtil.getCurrentWeekNumber())
        .build();

    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .dayOfWeek(DayOfWeek.THURSDAY)
        .interviewerId(interviewer.getId())
        .weekNum(WeekUtil.getCurrentWeekNumber())
        .build();

    List<InterviewerTimeSlot> interviewerTimeSlots =
        List.of(interviewerTimeSlot, interviewerTimeSlot1);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    given(interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(
        interviewer.getId(), interviewerTimeSlot.getWeekNum())).willReturn(interviewerTimeSlots);

    List<InterviewerTimeSlot> timeSlotsForCurrentWeek =
        interviewerService.getWeekTimeSlotsByInterviewerId(interviewer.getId(), true);

    assertThat(timeSlotsForCurrentWeek).isNotNull();
    assertThat(timeSlotsForCurrentWeek).hasSize(2);
  }


  @Test
  void givenInterviewerIdAndIsForCurrentWeekFalse_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {

    User interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .email("interrviewer@gmail.com")
        .role(UserRole.INTERVIEWER)
        .build();

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .from(startTime)
        .to(endTime)
        .dayOfWeek(DayOfWeek.FRIDAY)
        .interviewerId(interviewer.getId())
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .dayOfWeek(DayOfWeek.THURSDAY)
        .interviewerId(interviewer.getId())
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    List<InterviewerTimeSlot> interviewerTimeSlots =
        List.of(interviewerTimeSlot, interviewerTimeSlot1);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    given(interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(
        interviewer.getId(), interviewerTimeSlot.getWeekNum())).willReturn(interviewerTimeSlots);

    List<InterviewerTimeSlot> timeSlotsForNextWeek =
        interviewerService.getWeekTimeSlotsByInterviewerId(interviewer.getId(), false);

    assertThat(timeSlotsForNextWeek).isNotNull();
    assertThat(timeSlotsForNextWeek).hasSize(2);
  }

  @Test
  void givenNonExistingInterviewerId_whenGetWeekTimeSlots_thenThrowsException() {
    assertThrows(NotFoundException.class,
        () -> interviewerService.getWeekTimeSlotsByInterviewerId(UUID.randomUUID(), true));
  }


}
