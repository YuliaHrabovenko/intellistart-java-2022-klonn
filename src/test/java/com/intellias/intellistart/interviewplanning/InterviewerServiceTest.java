package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInterviewerPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Period;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerBookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PeriodRepository periodRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private InterviewerBookingLimitRepository interviewerBookingLimitRepository;

  @InjectMocks
  private InterviewerService interviewerService;

  private User interviewer;

  private Period period;

  @BeforeEach
  public void setup() {
    interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();

    period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 15, 30))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 17, 0))
        .build();
  }

  @Test
  void givenInterviewerTimeSlot_whenCreateInterviewerSlot_thenReturnInterviewerTimeSlot(){
    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
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
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .build();

    assertThrows(ResourceNotFoundException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenEndPeriodTimeBiggerThanStartPeriodTime_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 30))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 15, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }


  @Test
  void givenOutdatedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(2017, 11, 13, 15, 30))
        .to(LocalDateTime.of(2017, 11, 13, 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }


  @Test
  void givenUnroundedPeriod_whenCreateInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),15, 27))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),17, 0)).build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreatInterviewerSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(),15, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(interviewer.getId())).willReturn(Optional.of(interviewer));

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    assertThrows(InvalidInterviewerPeriodException.class,
        () -> interviewerService.createSlot(interviewerTimeSlot));

    verify(interviewerTimeSlotRepository, never()).save(any(InterviewerTimeSlot.class));
  }

  @Test
  void givenInterviewerSlotObject_whenUpdateInterviewerSlot_thenReturnUpdatedInterviewerSlot() {

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    Period newPeriod = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 14, 0))
        .to(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
            LocalDateTime.now().plusWeeks(1).getDayOfMonth(), 16, 0))
        .build();

    interviewerTimeSlot.setPeriodId(newPeriod.getId());

    InterviewerTimeSlot updatedSlot = interviewerService.updateSlot(interviewerTimeSlot);

    assertThat(updatedSlot.getPeriodId()).isEqualTo(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
  }

}
