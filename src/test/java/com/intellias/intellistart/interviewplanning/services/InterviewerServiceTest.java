package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
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

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    InterviewerTimeSlot savedSlot = interviewerService.createSlot(interviewerTimeSlot);

    System.out.println(savedSlot);

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

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

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
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }


//  @Test
//  void givenOutdatedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
//    Period period = Period.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .from(LocalTime.of(15, 30))
//        .to(LocalTime.of(17, 0))
//        .build();
//
//    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.MONDAY)
//        .build();
//
//    assertThrows(InvalidInterviewerPeriodException.class,
//        () -> interviewerService.createSlot(interviewerTimeSlot));
//
//    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
//  }


  @Test
  void givenUnroundedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(15, 27);
    LocalTime end = LocalTime.of(17, 0);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreatInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(15, 0);
    LocalTime end = LocalTime.of(16, 0);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodBefore8AM_whenCreatInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(7, 0);
    LocalTime end = LocalTime.of(16, 0);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenPeriodAfter10PM_whenCreateInterviewerSlot_thenThrowsException() {
    LocalTime start = LocalTime.of(10, 0);
    LocalTime end = LocalTime.of(23, 0);

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(ValidationException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

//  @Test
//  void givenPeriodsStartsAtWeekends_whenCreateInterviewerSlot_thenThrowsException() {
//    Period period = Period.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .from(LocalTime.of(10, 0))
//        .to(LocalTime.of(17, 0))
//        .build();
//
//    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.SUNDAY)
//        .build();
//
//    assertThrows(InvalidInterviewerPeriodException.class,
//        () -> interviewerService.createSlot(interviewerTimeSlot));
//
//    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
//  }

  @Test
  void givenInterviewerSlotObject_whenUpdateInterviewerSlot_thenReturnUpdatedInterviewerSlot() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    LocalTime startNew = LocalTime.of(14, 0);
    LocalTime endNew = LocalTime.of(16, 0);

    interviewerTimeSlot.setFrom(startNew);
    interviewerTimeSlot.setTo(endNew);

    InterviewerTimeSlot updatedSlot = interviewerService.updateSlot(interviewerTimeSlot);

    assertThat(updatedSlot.getFrom()).isEqualTo(startNew);
    assertThat(updatedSlot.getTo()).isEqualTo(endNew);
  }

//  @Test
//  void givenInterviewerBookingLimit_whenSetMaximumBookingsForNextWeek_thenReturnInterviewerBookingLimit(){
//    InterviewerBookingLimit interviewerBookingLimit = InterviewerBookingLimit.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .interviewerId(interviewer.getId())
//        .weekBookingLimit(3)
//        .currentBookingCount(0)
//        .build();
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//    given(interviewerBookingLimitRepository.save(interviewerBookingLimit)).willReturn(interviewerBookingLimit);
//
//    InterviewerBookingLimit savedInterviewerBookingLimit = interviewerService.setMaximumBookingsForNextWeek(interviewerBookingLimit);
//
//
//    assertThat(savedInterviewerBookingLimit.getWeekBookingLimit()).isEqualTo(3);
//    assertThat(savedInterviewerBookingLimit.getInterviewerId()).isEqualTo(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
//  }

  //cant get access to fields, gonna fix this in future commits
//  @Test
//  void givenInterviewerIdAndIsForCurrentWeekTrue_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {
//
//    User interviewer = User.builder().
//        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .email("candidate@gmail.com")
//        .role(UserRole.INTERVIEWER)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.MONDAY)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
//        .periodId(period.getId())
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.WEDNESDAY)
//        .build();
//
//    List<InterviewerTimeSlot> interviewerTimeSlots = List.of(interviewerTimeSlot, interviewerTimeSlot1);
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    given(interviewerTimeSlotRepository.findAll()).willReturn(interviewerTimeSlots);
//
//    for (InterviewerTimeSlot slot : interviewerTimeSlots){
//      given(periodRepository.findById(slot.getPeriodId())).willReturn(Optional.of(period));
//    }
//
//    List<InterviewerTimeSlot> slotList = interviewerService.getWeekTimeSlotsByInterviewerId(
//        interviewer.getId(), true);
//
//    assertThat(slotList).isNotNull();
//    assertThat(slotList).hasSize(2);
//  }
//
//
//  @Test
//  void givenInterviewerIdAndIsForCurrentWeekFalse_whenGetWeekTimeSlotsByInterviewerId_thenReturnInterviewerTimeSlotList() {
//
//    User interviewer = User.builder().
//        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .email("candidate@gmail.com")
//        .role(UserRole.INTERVIEWER)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.MONDAY)
//        .build();
//
//    InterviewerTimeSlot interviewerTimeSlot1 = InterviewerTimeSlot.builder()
//        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .periodId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
//        .interviewerId(interviewer.getId())
//        .dayOfWeek(DayOfWeek.WEDNESDAY)
//        .build();
//
//    List<InterviewerTimeSlot> interviewerTimeSlots = List.of(interviewerTimeSlot, interviewerTimeSlot1);
//
//    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));
//
//    given(interviewerTimeSlotRepository.findAll()).willReturn(interviewerTimeSlots);
//
//    List<InterviewerTimeSlot> slotList = interviewerService.getWeekTimeSlotsByInterviewerId(
//        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"), false);
//
//    assertThat(slotList).isNotNull();
//    assertThat(slotList).hasSize(2);
//  }

}
