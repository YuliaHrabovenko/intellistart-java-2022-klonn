package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.BookingDoneException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Period;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.PeriodRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.CandidateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  @Mock
  private PeriodRepository periodRepository;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private CandidateService candidateService;

  private User candidate;

  private Period period;

  @BeforeEach
  public void setup() {
    candidate = User.builder().
        id(1L).
        email("candidate@gmail.com").
        role(UserRole.CANDIDATE)
        .build();

    period = Period.builder()
        .id(1L)
        .from(LocalDateTime.of(2022, 11, 13, 15, 30))
        .to(LocalDateTime.of(2022, 11, 13, 17, 0))
        .build();
  }

  @Test
  void givenCandidateSlotObject_whenCreateCandidateSlot_thenReturnCandidateSlot() {

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

//    System.out.println(period);

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

//    System.out.println(candidate);

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    given(candidateTimeSlotRepository.save(candidateTimeSlot)).willReturn(candidateTimeSlot);

    CandidateTimeSlot savedSlot = candidateService.createSlot(candidateTimeSlot);

    System.out.println(savedSlot);

    assertThat(savedSlot).isNotNull();
  }

  @Test
  void givenNonExistPeriodIdAndCandidateId_whenCreateCandidateSlot_thenThrowsException() {
//    given(periodRepository.findById(period.getId())).willReturn(Optional.empty());

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    assertThrows(ResourceNotFoundException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenEndPeriodTimeBiggerThanStartPeriodTime_whenCreateCandidateSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(2L)
        .from(LocalDateTime.of(2022, 11, 13, 17, 0))
        .to(LocalDateTime.of(2022, 11, 13, 15, 30))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    assertThrows(InvalidPeriodException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenOutdatedPeriod_whenCreateCandidateSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(1L)
        .from(LocalDateTime.of(2017, 11, 13, 15, 30))
        .to(LocalDateTime.of(2017, 11, 13, 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    assertThrows(InvalidPeriodException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenUnroundedPeriod_whenCreateCandidateSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(1L)
        .from(LocalDateTime.of(2022, 11, 13, 15, 27))
        .to(LocalDateTime.of(2022, 11, 13, 17, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    assertThrows(InvalidPeriodException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreateCandidateSlot_thenThrowsException() {
    Period period = Period.builder()
        .id(1L)
        .from(LocalDateTime.of(2022, 11, 13, 15, 0))
        .to(LocalDateTime.of(2022, 11, 13, 16, 0))
        .build();

    given(periodRepository.findById(period.getId())).willReturn(Optional.of(period));

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    assertThrows(InvalidPeriodException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenCandidateSlotObject_whenUpdateCandidateSlot_thenReturnUpdatedCandidateSlot() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    given(candidateTimeSlotRepository.save(candidateTimeSlot)).willReturn(candidateTimeSlot);
    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));

    Period periodNew = Period.builder()
        .id(15L)
        .from(LocalDateTime.of(2022, 11, 20, 14, 0))
        .to(LocalDateTime.of(2022, 11, 20, 16, 0))
        .build();

    candidateTimeSlot.setPeriodId(periodNew.getId());

    CandidateTimeSlot updatedSlot = candidateService.updateSlot(candidateTimeSlot);

//    System.out.println(updatedSlot);

    assertThat(updatedSlot.getPeriodId()).isEqualTo(15L);
  }

  @Test
  void givenBookedCandidateSlotObject_whenUpdateCandidateSlot_thenThrowsException() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));
//    System.out.println(candidateTimeSlot);

    Booking booking = Booking.builder()
        .id(1L)
        .periodId(5L)
        .candidateTimeSlotId(1L)
        .interviewerTimeSlotId(2L)
        .build();

    List<Booking> bookings = List.of(booking);
    when(bookingRepository.findAll()).thenReturn(bookings);

    candidateTimeSlot.setPeriodId(105L);
    assertThrows(BookingDoneException.class, () -> candidateService.updateSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenCandidateTimeSlotId_whenDeleteCandidateTimeSlot_thenNothing() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));

    willDoNothing().given(candidateTimeSlotRepository).deleteById(candidateTimeSlot.getId());

    candidateService.deleteSlot(candidateTimeSlot.getId());

    verify(candidateTimeSlotRepository, times(1)).deleteById(candidateTimeSlot.getId());
  }

  @Test
  void givenCandidateTimeSlotId_whenDeleteCandidateTimeSlot_thenThrowsException() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(period.getId())
        .candidateId(candidate.getId())
        .build();

    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> candidateService.deleteSlot(candidateTimeSlot.getId()));

    verify(candidateTimeSlotRepository, times(0)).deleteById(candidateTimeSlot.getId());
  }


  @Test
  void givenCandidateId_whenGetCandidateTimeSlots_thenReturnCandidateTimeSlotList() {
    // given - precondition or setup

    User candidate = User.builder().
        id(1L).
        email("candidate@gmail.com").
        role(UserRole.CANDIDATE)
        .build();

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(1L)
        .candidateId(candidate.getId())
        .build();

    CandidateTimeSlot candidateTimeSlot2 = CandidateTimeSlot.builder()
        .id(2L)
        .periodId(2L)
        .candidateId(candidate.getId())
        .build();

    List<CandidateTimeSlot> candidateTimeSlots = List.of(candidateTimeSlot, candidateTimeSlot2);

    given(userRepository.findById(candidate.getId())).willReturn(Optional.of(candidate));

    given(candidateTimeSlotRepository.findAll()).willReturn(candidateTimeSlots);

    List<CandidateTimeSlot> slotList = candidateService.getSlotsByCandidateId(1L);

    assertThat(slotList).isNotNull();
    assertThat(slotList).hasSize(2);
  }

  @Test
  void givenCandidateTimeSlotId_whenGetCandidateSlotBookings_thenReturnBookingList() {
    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(1L)
        .periodId(1L)
        .candidateId(candidate.getId())
        .build();
    candidateTimeSlotRepository.save(candidateTimeSlot);

    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));

    Booking booking1 = Booking.builder()
        .id(1L)
        .candidateTimeSlotId(candidateTimeSlot.getId())
        .interviewerTimeSlotId(10L)
        .periodId(5L)
        .build();

    Booking booking2 = Booking.builder()
        .id(2L)
        .candidateTimeSlotId(candidateTimeSlot.getId())
        .interviewerTimeSlotId(3L)
        .periodId(2L)
        .build();

    given(candidateService.getBookingsByCandidateSlotId(candidateTimeSlot.getId())).willReturn(
        List.of(booking1, booking2));

    List<Booking> bookings =
        candidateService.getBookingsByCandidateSlotId(candidateTimeSlot.getId());
    assertThat(bookings).isNotNull();
    assertThat(bookings).hasSize(2);
  }

}
