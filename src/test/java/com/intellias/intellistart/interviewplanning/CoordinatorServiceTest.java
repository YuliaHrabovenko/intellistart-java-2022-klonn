package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.BookingStatus;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.Period;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import java.time.LocalDateTime;
import java.time.Month;
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
  private User candidate;
  private User coordinator;

  @BeforeEach
  public void setup() {
    interviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();

    candidate = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001")).
        email("candidate@gmail.com").
        role(UserRole.CANDIDATE)
        .build();

    coordinator = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002")).
        email("coordanator@gmail.com").
        role(UserRole.COORDINATOR)
        .build();
  }

  @Test
  void SuccessUpdatingOfBooking() {
    Booking booking = new Booking(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"),
        UUID.fromString("123e4567-e89b-42d3-a456-556642440001"),
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"),
        BookingStatus.BOOKED,
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
    Period period = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .from(LocalDateTime.of(2022, Month.OCTOBER, 12, 10, 0))
        .to(LocalDateTime.of(2022, Month.OCTOBER, 12, 11, 30))
        .build();

    InterviewerTimeSlot interviewerTimeSlot = InterviewerTimeSlot.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
        .periodId(period.getId())
        .interviewerId(interviewer.getId())
        .build();

    Period periodNew = Period.builder()
        .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440015"))
        .from(LocalDateTime.of(2022, 11, 20, 14, 0))
        .to(LocalDateTime.of(2022, 11, 20, 16, 0))
        .build();

    given(interviewerTimeSlotRepository.findById(interviewerTimeSlot.getId())).willReturn(
        Optional.of(interviewerTimeSlot));

    given(interviewerTimeSlotRepository.save(interviewerTimeSlot)).willReturn(interviewerTimeSlot);
    interviewerTimeSlot.setPeriodId(periodNew.getId());
    InterviewerTimeSlot updated =
        coordinatorService.updateInterviewerTimeSlot(interviewerTimeSlot, interviewer.getId());
    assertThat(updated.getPeriodId()).isEqualTo(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440015"));
  }

  @Test
  void SuccessGrantCoordinatorRole() {
    User grantedInterviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.COORDINATOR)
        .build();

    given(userRepository.findUserByEmail("interviewer@gmail.com")).willReturn(
        Optional.of(interviewer));

    given(userRepository.save(interviewer)).willReturn(
        grantedInterviewer);

    User user = coordinatorService.grantInterviewerRole("interviewer@gmail.com");

    assertEquals(UserRole.COORDINATOR, user.getRole());
  }

  @Test
  void SuccessGrantInterviewerRole() {
    User grantedInterviewer = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.INTERVIEWER)
        .build();

    given(userRepository.findUserByEmail("candidate@gmail.com")).willReturn(
        Optional.of(candidate));

    given(userRepository.save(candidate)).willReturn(
        grantedInterviewer);

    User user = coordinatorService.grantInterviewerRole("candidate@gmail.com");

    assertEquals(UserRole.INTERVIEWER, user.getRole());
  }

  @Test
  void SuccessRevokeCoordinatorRole() {
    User coordinatorWithoutCoordinatorRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440002")).
        email("coordinator@gmail.com").
        role(UserRole.CANDIDATE)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"))).willReturn(
        Optional.of(coordinator));

    given(userRepository.save(coordinator)).willReturn(coordinatorWithoutCoordinatorRole);

    User user = null;
    user = coordinatorService.revokeCoordinatorRole(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440002"));
    assertEquals(UserRole.CANDIDATE, user.getRole());
  }

  @Test
  void SuccessRevokeInterviewerRole() {
    User interviewerWithoutInterviewerRole = User.builder().
        id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000")).
        email("interviewer@gmail.com").
        role(UserRole.CANDIDATE)
        .build();

    given(userRepository.findById(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).willReturn(
        Optional.of(interviewer));

    given(userRepository.save(interviewer)).willReturn(interviewerWithoutInterviewerRole);
    User user = null;
    user = coordinatorService.revokeInterviewerRole(
        UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
    assertEquals(UserRole.CANDIDATE, user.getRole());

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