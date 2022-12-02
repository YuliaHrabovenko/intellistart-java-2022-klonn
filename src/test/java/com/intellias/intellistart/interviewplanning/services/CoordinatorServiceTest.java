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

import com.intellias.intellistart.interviewplanning.exceptions.AuthException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService.DayInfo;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  @Mock
  private CandidateTimeSlotRepository candidateTimeSlotRepository;
  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private CoordinatorService coordinatorService;

  @Mock
  private InterviewerService interviewerService;

  @InjectMocks
  private BookingService bookingService;

  private User interviewer;
  private User coordinator;
  private LocalTime startTime;
  private LocalTime endTime;
  private InterviewerTimeSlot interviewerSlot;
  private CandidateTimeSlot candidateSlot;
  private Booking booking;

  @BeforeEach
  public void setup() {
    interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();

    coordinator = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002")).
        email("coordanator@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    interviewerSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440015"))
        .dayOfWeek(DayOfWeek.THURSDAY)
        .weekNum("202243")
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .interviewerId(UUID.fromString("123e4567-e89b-42d3-a456-556642440011"))
        .build();

    candidateSlot = CandidateTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440016"))
        .date(LocalDate.of(2022, 10, 27))
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .build();

    booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .candidateTimeSlotId(candidateSlot.getId())
        .interviewerTimeSlotId(interviewerSlot.getId()).build();

    interviewerSlot.setBookingList(List.of(booking));

    startTime = LocalTime.of(10, 0);
    endTime = LocalTime.of(11, 30);
  }

  @Test
  void givenEmail_whenGrantCoordinatorRole_thenReturnUser() {
    User coordinator = User.builder().
        email("coordinator@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    given(userRepository.save(coordinator)).willReturn(coordinator);
    User user = coordinatorService.grantCoordinatorRole(coordinator);
    assertEquals(UserRole.COORDINATOR, user.getRole());
  }

  @Test
  void givenEmail_whenGrantCoordinatorRole_throwException() {
    given(userRepository.findByEmail(coordinator.getEmail())).willReturn(
        Optional.of(coordinator));
    assertThrows(ValidationException.class,
        () -> coordinatorService.grantCoordinatorRole(coordinator));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void givenEmail_whenGrantInterviewerRole_thenReturnUser() {
    User interviewer = User.builder().
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();
    given(userRepository.save(interviewer)).willReturn(interviewer);
    User user = coordinatorService.grantInterviewerRole(interviewer);
    assertEquals(UserRole.INTERVIEWER, user.getRole());
  }

  @Test
  void givenEmail_whenGrantInterviewerRole_throwException() {
    given(userRepository.findByEmail(interviewer.getEmail())).willReturn(
        Optional.of(interviewer));
    assertThrows(ValidationException.class,
        () -> coordinatorService.grantInterviewerRole(interviewer));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void givenBookingId_whenDeleteBooking_thenNothing() {

    Booking booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalTime.of(15, 30))
        .to(LocalTime.of(17, 0))
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
  void givenBookingId_whenDeleteBooking_thenThrowsException() {

    Booking booking = Booking.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalTime.of(15, 30))
        .to(LocalTime.of(17, 0))
        .subject("Subject")
        .description("Description")
        .build();

    given(bookingRepository.findById(booking.getId())).willReturn(
        Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.deleteBooking(booking.getId()));

    verify(bookingRepository, times(0)).delete(booking);
  }

  @Test
  void givenInterviewerSlotWithNotValidWeekNum_whenUpdateInterviewerSlot_thenThrowException() {
    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(startTime)
        .to(endTime)
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum("201840")
        .interviewerId(interviewer.getId())
        .build();

    assertThrows(ValidationException.class,
        () -> coordinatorService.updateInterviewerTimeSlot(interviewerTimeSlot,
            interviewerTimeSlot.getInterviewerId(), interviewerTimeSlot.getId()));

  }

  @Test
  void givenInterviewerSlot_whenUpdateInterviewerSlot_thenReturnUpdatedSlot() {
    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))
        .from(startTime)
        .to(endTime)
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .build();

    given(interviewerService.updateSlot(interviewerTimeSlot,
        interviewer.getId(),
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(interviewerTimeSlot);

    InterviewerTimeSlot updatedSlot =
        coordinatorService.updateInterviewerTimeSlot(interviewerTimeSlot,
            interviewer.getId(),
            UUID.fromString("123e4567-e89b-42d3-a456-556642440002"));

    assertThat(updatedSlot.getFrom()).isEqualTo(updatedSlot.getFrom());
    assertThat(updatedSlot.getTo()).isEqualTo(updatedSlot.getTo());
  }

  @Test
  void givenWeekNum_whenGetSlotsAndBookings_thenReturnSlotsAndBookings() {
    String weekNum = "202243";

    Map<DayOfWeek, List<InterviewerTimeSlot>> interviewerSlotsByDayOfWeek =
        Map.of(interviewerSlot.getDayOfWeek(), List.of(interviewerSlot));

    given(interviewerTimeSlotRepository.findByWeekNum(
        interviewerSlot.getWeekNum())).willReturn(List.of(interviewerSlot));

    assertThat(
        coordinatorService.getInterviewerSlotsByDayOfWeek(interviewerSlot.getWeekNum())).isEqualTo(
        interviewerSlotsByDayOfWeek);

    Map<LocalDate, List<CandidateTimeSlot>> candidateSlotsByDate =
        Map.of(candidateSlot.getDate(), List.of(candidateSlot));

    given(candidateTimeSlotRepository.findByDateBetween(
        LocalDate.of(2022, 10, 27),
        LocalDate.of(2022, 10, 27).plusDays(4L))).willReturn(List.of(candidateSlot));

    assertThat(coordinatorService.getCandidateSlotsByDate(candidateSlot.getDate())).isEqualTo(
        candidateSlotsByDate);

    DayOfWeek[] dayOfWeeks = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};

    LocalDate[] dates = {LocalDate.of(2022, 10, 24),
        LocalDate.of(2022, 10, 25),
        LocalDate.of(2022, 10, 26),
        LocalDate.of(2022, 10, 27),
        LocalDate.of(2022, 10, 28)};

    DayInfo[] daysInfo =
        {new DayInfo(), new DayInfo(), new DayInfo(), new DayInfo(), new DayInfo()};
    for (int i = 0; i < daysInfo.length; i++) {
      daysInfo[i].setDayOfWeek(dayOfWeeks[i]);
      daysInfo[i].setDate(dates[i]);
    }

    daysInfo[3].setInterviewerTimeSlots(List.of(interviewerSlot));
    daysInfo[3].setBookings(Map.of(booking.getId(), booking));
    DayInfo[] expected = coordinatorService.getAllSlotsAndBookingsGroupedByDay(weekNum).get("days");

    assertThat(expected[3].getCandidateTimeSlots()).isEqualTo(daysInfo[3].getCandidateTimeSlots());
    assertThat(expected[3].getInterviewerTimeSlots()).isEqualTo(
        daysInfo[3].getInterviewerTimeSlots());
    assertThat(expected[3].getBookings()).isEqualTo(daysInfo[3].getBookings());
    assertThat(expected[3].getDayOfWeek()).isEqualTo(daysInfo[3].getDayOfWeek());
    assertThat(expected[3].getDate()).isEqualTo(daysInfo[3].getDate());
  }

  @Test
  void givenCoordinatorId_whenRevokeCoordinatorRole_thenNothing() {
    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.of(coordinator));

    coordinatorService.revokeCoordinatorRole(coordinator.getId(), "test@gmail.com");
    verify(userRepository, times(1)).deleteById(coordinator.getId());
  }

  @Test
  void givenCoordinatorId_whenRevokeCoordinatorRole_thenThrowException() {
    User coordinatorWithInterviewerRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001")).
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))).willReturn(
        Optional.of(coordinatorWithInterviewerRole));


    assertThrows(AuthException.class,
        () -> coordinatorService.revokeCoordinatorRole(coordinatorWithInterviewerRole.getId(), "test@gmail.com"));

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void givenNonExistingCoordinator_whenRevokeCoordinatorRole_thenThrowException() {
    User coordinator = User.builder().
        email("coord@gmail.com")
        .build();

    coordinatorService.grantCoordinatorRole(coordinator);

    assertThrows(NotFoundException.class,
        () -> coordinatorService.revokeCoordinatorRole(UUID.randomUUID(), coordinator.getEmail()));

    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  void givenCoordinator_whenRevokeCoordinatorRole_thenThrowException() {
    User coordinator = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
        .email("coord@gmail.com")
        .role(UserRole.COORDINATOR)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))).willReturn(
            Optional.of(coordinator));

    assertThrows(ValidationException.class,
        () -> coordinatorService.revokeCoordinatorRole(coordinator.getId(), coordinator.getEmail()));

    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  void givenInterviewerId_whenRevokeInterviewerRole_thenNothing() {
    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).willReturn(
        Optional.of(interviewer));

    coordinatorService.revokeInterviewerRole(interviewer.getId());
    verify(userRepository, times(1)).deleteById(interviewer.getId());
  }

  @Test
  void givenNonExistingInterviewerId_whenRevokeInterviewerRole_thenThrowException() {
    User interviewer = User.builder().
        email("interviwer@gmail.com")
        .build();

    coordinatorService.grantInterviewerRole(interviewer);

    assertThrows(NotFoundException.class,
        () -> coordinatorService.revokeInterviewerRole(UUID.randomUUID()));

    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  void givenInterviewerId_whenRevokeInterviewerRole_thenThrowException() {
    User interviewerWithCoordinatorRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440003")).
        email("interviewer@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440003"))).willReturn(
        Optional.of(interviewerWithCoordinatorRole));

    assertThrows(AuthException.class,
        () -> coordinatorService.revokeInterviewerRole(interviewerWithCoordinatorRole.getId()));

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void WhenGetCoordinators_ReturnCoordinators() {
    User coordinator1 = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440003")).
        email("interviewer1@gmail.com").
        role(UserRole.COORDINATOR)
        .build();
    List<User> coordinators = List.of(coordinator1, coordinator);
    given(userRepository.findByRole(UserRole.COORDINATOR)).willReturn(coordinators);

    List<User> users = coordinatorService.getCoordinators();

    assertThat(users).isEqualTo(coordinators);
    assertEquals(UserRole.COORDINATOR, coordinators.get(1).getRole());
    assertEquals(UserRole.COORDINATOR, coordinators.get(0).getRole());
  }

  @Test
  void WhenGetInterviewers_ReturnInterviewers() {
    User interviewer1 = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440003")).
        email("interviewer1@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();
    List<User> interviewers = List.of(interviewer1, interviewer);
    given(userRepository.findByRole(UserRole.INTERVIEWER)).willReturn(interviewers);
    List<User> users = coordinatorService.getInterviewers();

    assertThat(users).isEqualTo(interviewers);
    assertEquals(UserRole.INTERVIEWER, interviewers.get(1).getRole());
    assertEquals(UserRole.INTERVIEWER, interviewers.get(0).getRole());
  }
}