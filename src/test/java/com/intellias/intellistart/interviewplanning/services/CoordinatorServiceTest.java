package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidResourceException;
import com.intellias.intellistart.interviewplanning.exceptions.ResourceException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
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
class CoordinatorServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private CoordinatorService coordinatorService;
  private User interviewer;
  private User coordinator;
  private LocalTime startTime;
  private LocalTime endTime;

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
    User user = coordinatorService.grantCoordinatorRole("coordinator@gmail.com");
    assertEquals(UserRole.COORDINATOR, user.getRole());
  }

  @Test
  void givenEmail_whenGrantCoordinatorRole_throwException() {
    given(userRepository.findUserByEmail(coordinator.getEmail())).willReturn(Optional.of(coordinator));
    assertThrows(InvalidResourceException.class,
        () -> coordinatorService.grantCoordinatorRole(coordinator.getEmail()));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void givenEmail_whenGrantInterviewerRole_thenReturnUser() {
    User interviewer = User.builder().
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();
    given(userRepository.save(interviewer)).willReturn(interviewer);
    User user = coordinatorService.grantInterviewerRole("interviewer@gmail.com");
    assertEquals(UserRole.INTERVIEWER, user.getRole());
  }
  @Test
  void givenEmail_whenGrantInterviewerRole_throwException() {
    given(userRepository.findUserByEmail(interviewer.getEmail())).willReturn(Optional.of(interviewer));
    assertThrows(InvalidResourceException.class,
        () -> coordinatorService.grantCoordinatorRole(interviewer.getEmail()));
    verify(userRepository, never()).save(any(User.class));
  }
  @Test
  void SuccessUpdatingOfBooking() {
    Booking booking = new Booking(startTime,
        endTime,
        UUID.fromString("123e4567-e89b-42d3-a456-556642440001"),
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"),
        "Subject",
        "Description"
    );
    given(bookingRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).willReturn(
        Optional.of(booking));

    given(bookingRepository.save(booking)).willReturn(booking);
    Booking booking1 = null;
    booking1 = coordinatorService.updateBooking(booking,
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
    assertThat(booking1).isNotNull();
  }

  @Test
  void SuccessUpdateInterviewerTimeSlot() {

    LocalTime start = LocalTime.of(10, 0);
    LocalTime end = LocalTime.of(11, 30);

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(start)
        .to(end)
        .interviewerId(interviewer.getId())
        .build();

    LocalTime startNew = LocalTime.of(14, 0);
    LocalTime endNew = LocalTime.of(16, 0);

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);
    interviewerTimeSlot.setFrom(startNew);
    interviewerTimeSlot.setTo(endNew);
    InterviewerTimeSlot updated =
        coordinatorService.updateInterviewerTimeSlot(interviewerTimeSlot, interviewer.getId());
    assertThat(updated.getFrom()).isEqualTo(startNew);
    assertThat(updated.getTo()).isEqualTo(endNew);
  }

  @Test
  void SuccessRevokeCoordinatorRole() {
    User coordinatorWithoutCoordinatorRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002")).
        email("coordinator@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.of(coordinator));

    given(userRepository.save(coordinator)).willReturn(coordinatorWithoutCoordinatorRole);

    User user = null;
    user = coordinatorService.revokeCoordinatorRole(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"));
    assertEquals(UserRole.COORDINATOR, user.getRole());
  }

  @Test
  void SuccessRevokeInterviewerRole() {
    User interviewerWithoutInterviewerRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).willReturn(
        Optional.of(interviewer));

    given(userRepository.save(interviewer)).willReturn(interviewerWithoutInterviewerRole);
    User user = null;
    user = coordinatorService.revokeInterviewerRole(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
    assertEquals(UserRole.COORDINATOR, user.getRole());

  }

  @Test
  void SuccessGetCoordinators() {
    User coordinator1 = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440003")).
        email("interviewer1@gmail.com").
        role(UserRole.COORDINATOR)
        .build();
    List<User> coordinators = List.of(coordinator1, coordinator);
    given(userRepository.findByRole(UserRole.COORDINATOR)).willReturn(coordinators);

    List<User> users = userRepository.findByRole(UserRole.COORDINATOR);

    assertThat(coordinators).hasSize(2);
    assertEquals(UserRole.COORDINATOR, coordinators.get(1).getRole());
    assertEquals(UserRole.COORDINATOR, coordinators.get(0).getRole());
  }

  @Test
  void SuccessGetInterviewers() {
    User interviewer1 = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440003")).
        email("interviewer1@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();
    List<User> interviewers = List.of(interviewer1, interviewer);
    given(userRepository.findByRole(UserRole.INTERVIEWER)).willReturn(interviewers);

    List<User> users = userRepository.findByRole(UserRole.INTERVIEWER);
    assertEquals(UserRole.INTERVIEWER, interviewers.get(1).getRole());
    assertEquals(UserRole.INTERVIEWER, interviewers.get(0).getRole());
  }
}
