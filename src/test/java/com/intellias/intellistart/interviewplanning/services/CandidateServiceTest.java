package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import java.time.LocalDate;
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
class CandidateServiceTest {

  @Mock
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private CandidateService candidateService;

  private LocalDate date;
  private LocalTime from;
  private LocalTime to;

  @BeforeEach
  public void setup() {
    from = LocalTime.of(15, 30);
    to = LocalTime.of(17, 0);
    date = LocalDate.now().plusWeeks(2L);
  }

  @Test
  void givenCandidateSlotObject_whenCreateCandidateSlot_thenReturnCandidateSlot() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(from)
        .to(to)
        .date(date)
        .build();

    given(candidateTimeSlotRepository.save(candidateTimeSlot)).willReturn(candidateTimeSlot);

    CandidateTimeSlot savedSlot = candidateService.createSlot(candidateTimeSlot);

    assertThat(savedSlot).isNotNull();
  }

  @Test
  void givenEndPeriodTimeBiggerThanStartPeriodTime_whenCreateCandidateSlot_thenThrowsException() {

    LocalTime startTime = LocalTime.of(17, 0);
    LocalTime endTime = LocalTime.of(15, 30);

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .date(date)
        .build();

    assertThrows(ValidationException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenOutdatedDate_whenCreateCandidateSlot_thenThrowsException() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(from)
        .to(to)
        .date(LocalDate.of(2022, 10, 1))
        .build();

    assertThrows(ValidationException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenUnroundedPeriod_whenCreateCandidateSlot_thenThrowsException() {

    LocalTime startTime = LocalTime.of(15, 27);
    LocalTime endTime = LocalTime.of(17, 0);

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .date(date)
        .build();

    assertThrows(ValidationException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenSmallPeriod_whenCreateCandidateSlot_thenThrowsException() {

    LocalTime startTime = LocalTime.of(15, 0);
    LocalTime endTime = LocalTime.of(16, 0);

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .date(date)
        .build();

    assertThrows(ValidationException.class,
        () -> candidateService.createSlot(candidateTimeSlot));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenCandidateSlotObject_whenUpdateCandidateSlot_thenReturnUpdatedCandidateSlot() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(from)
        .to(to)
        .date(LocalDate.now().plusWeeks(2L))
        .build();

    given(candidateTimeSlotRepository.save(candidateTimeSlot)).willReturn(candidateTimeSlot);
    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));

    LocalTime startTimeNew = LocalTime.of(14, 30);
    LocalTime endTimeNew = LocalTime.of(16, 0);

    candidateTimeSlot.setFrom(startTimeNew);
    candidateTimeSlot.setTo(endTimeNew);

    CandidateTimeSlot updatedSlot =
        candidateService.updateSlot(candidateTimeSlot, candidateTimeSlot.getId());

    assertThat(updatedSlot.getFrom()).isEqualTo(startTimeNew);
    assertThat(updatedSlot.getTo()).isEqualTo(endTimeNew);
  }

  @Test
  void givenNonExistingSlot_whenUpdateCandidateSlot_thenThrowsException() {

    LocalTime startTime = LocalTime.of(15, 27);
    LocalTime endTime = LocalTime.of(17, 0);

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .from(startTime)
        .to(endTime)
        .date(date)
        .build();

    assertThrows(NotFoundException.class,
        () -> candidateService.updateSlot(candidateTimeSlot, UUID.randomUUID()));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }

  @Test
  void givenBookedCandidateSlotObject_whenUpdateCandidateSlot_thenThrowsException() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(from)
        .to(to)
        .date(date)
        .build();

    LocalTime startTimeNew = LocalTime.of(16, 30);
    LocalTime endTimeNew = LocalTime.of(18, 0);

    given(candidateTimeSlotRepository.findById(candidateTimeSlot.getId())).willReturn(
        Optional.of(candidateTimeSlot));

    Booking booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(from)
        .to(to)
        .candidateTimeSlotId(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .interviewerTimeSlotId(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .build();

    List<Booking> bookings = List.of(booking);
    when(bookingRepository.findByCandidateTimeSlotId(candidateTimeSlot.getId())).thenReturn(
        bookings);

    candidateTimeSlot.setFrom(startTimeNew);
    candidateTimeSlot.setTo(endTimeNew);
    assertThrows(ValidationException.class,
        () -> candidateService.updateSlot(candidateTimeSlot, candidateTimeSlot.getId()));

    verify(candidateTimeSlotRepository, never()).save(any(CandidateTimeSlot.class));
  }


  @Test
  void givenCandidateEmail_whenGetCandidateTimeSlots_thenReturnCandidateTimeSlotList() {

    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .date(date)
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .email("test@gmail.com")
        .build();

    CandidateTimeSlot candidateTimeSlot2 = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .date(date.plusWeeks(1L))
        .from(LocalTime.of(15, 0))
        .to(LocalTime.of(16, 30))
        .email("test@gmail.com")
        .build();

    List<CandidateTimeSlot> candidateTimeSlots = List.of(candidateTimeSlot, candidateTimeSlot2);


    given(candidateTimeSlotRepository.findByEmail("test@gmail.com")).willReturn(
        candidateTimeSlots);


    List<CandidateTimeSlot> slotList = candidateService.getSlotsByCandidateEmail("test@gmail.com");

    assertThat(slotList).isNotNull();
    assertThat(slotList).hasSize(2);
  }

}
