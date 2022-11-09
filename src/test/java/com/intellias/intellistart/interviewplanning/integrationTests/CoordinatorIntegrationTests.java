package com.intellias.intellistart.interviewplanning.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoordinatorIntegrationTests {

  @LocalServerPort
  private int port;

  private String baseUrl = "http://localhost";

  private static RestTemplate restTemplate;

  @Autowired
  private UserTestRepository userTestRepository;

  @Autowired
  private InterviewerTimeSlotRepoTest interviewerTimeSlotRepoTest;

  @Autowired
  private CandidateTimeSlotRepoTest candidateTimeSlotRepoTest;

  @Autowired
  private BookingRepo bookingRepo;

  @BeforeAll
  public static void init() {
    restTemplate = new RestTemplate();
  }

  @BeforeEach
  public void setUp() {
    baseUrl = baseUrl.concat(":").concat(port + "");
    userTestRepository.deleteAll();
  }

  @Test
  void testGetInterviewersSuccess() {
    User interviewer1 = new User("Firstinterviewer@gmail.com", UserRole.INTERVIEWER);
    User interviewer2 = new User("Secondinterviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer1);
    userTestRepository.save(interviewer2);

    User coordinator1 = new User("FirstCoordinator@gmail.com", UserRole.COORDINATOR);
    User coordinator2 = new User("SecondCoordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);
    userTestRepository.save(coordinator2);

    assertEquals(4, userTestRepository.findAll().size());

    List<User> users = restTemplate.getForObject(baseUrl + "/users/interviewers", List.class);
    assertEquals(2,users.size());

  }

  @Test
  void testGetCoordinatorsSuccess() {
    User interviewer1 = new User("Firstinterviewer@gmail.com", UserRole.INTERVIEWER);
    User interviewer2 = new User("Secondinterviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer1);
    userTestRepository.save(interviewer2);

    User coordinator1 = new User("FirstCoordinator@gmail.com", UserRole.COORDINATOR);
    User coordinator2 = new User("SecondCoordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);
    userTestRepository.save(coordinator2);

    assertEquals(4, userTestRepository.findAll().size());

    List<User> users = restTemplate.getForObject(baseUrl + "/users/coordinators", List.class);
    assertEquals(2,users.size());

  }

  @Test
  void testCreateInterviewerByIEmailSuccess() {
    baseUrl = baseUrl.concat("/users/interviewers");
    HttpEntity<User> request = new HttpEntity<>(new User("FirstInterviewer@gmail.com"));
    ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, userTestRepository.findAll().size());
  }

  @Test
  void testCreateCoordinatorByEmailSuccess() {
    baseUrl = baseUrl.concat("/users/coordinators");
    HttpEntity<User> request = new HttpEntity<>(new User("FirstCoordinator@gmail.com"));
    ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, userTestRepository.findAll().size());
  }

  @Test
  void testDeleteInterviewerByIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);
    assertEquals(1, userTestRepository.findAll().size());

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();
    restTemplate.delete(baseUrl + "/users/interviewers/{interviewerId}", interviewerID);

    assertEquals(0, userTestRepository.findAll().size());
  }

  @Test
  void testDeleteCoordinatorByIdSuccess() {
    User coordinator = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator);
    assertEquals(1, userTestRepository.findAll().size());

    UUID coordinatorID = userTestRepository.findUserByEmail(coordinator.getEmail()).get().getId();
    restTemplate.delete(baseUrl + "/users/coordinators/{coordinatorId}", coordinatorID);

    assertEquals(0, userTestRepository.findAll().size());
  }

  @Test
  void testUpdateInterviewerTimeSlotSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<InterviewerTimeSlot> requestCreate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .weekNum(WeekUtil.getNextWeekNumber())
            .dayOfWeek(DayOfWeek.FRIDAY)
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .build());

    String URLForCreate = baseUrl + "/interviewers/" + interviewerID + "/slots";
    ResponseEntity<InterviewerTimeSlot> responseCreate = restTemplate.exchange(
        URLForCreate, HttpMethod.POST, requestCreate, InterviewerTimeSlot.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, responseCreate.getStatusCode());

    HttpEntity<InterviewerTimeSlot> requestUpdate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .weekNum(WeekUtil.getNextWeekNumber())
            .dayOfWeek(DayOfWeek.FRIDAY)
            .from(LocalTime.of(12, 0))
            .to(LocalTime.of(16, 30))
            .interviewerId(interviewer.getId())
            .build());

    String URLToUpdate = baseUrl + "/interviewers/" + interviewerID + "/next-week-slots/" +
        interviewerTimeSlotRepoTest.findAll().get(0).getId();


    ResponseEntity<InterviewerTimeSlot> responseUpdate = restTemplate.exchange(
        URLToUpdate, HttpMethod.POST, requestUpdate, InterviewerTimeSlot.class);

    assertEquals(HttpStatus.OK, responseUpdate.getStatusCode());

  }

  @Test
  void testCreateBookingSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    User candidate = new User("candidate@gmail.com");
    userTestRepository.save(candidate);

    InterviewerTimeSlot interviewerTimeSlot =
        InterviewerTimeSlot.builder()
            .weekNum(WeekUtil.getNextWeekNumber())
            .dayOfWeek(DayOfWeek.FRIDAY)
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .build();

    interviewerTimeSlotRepoTest.save(interviewerTimeSlot);
    assertEquals(1, interviewerTimeSlotRepoTest.findAll().size());

    CandidateTimeSlot candidateTimeSlot =
        CandidateTimeSlot.builder()
            .date(LocalDate.now())
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .build();

    candidateTimeSlotRepoTest.save(candidateTimeSlot);
    assertEquals(1, candidateTimeSlotRepoTest.findAll().size());

    HttpEntity<Booking> request = new HttpEntity<>(
        Booking.builder()
            .from(LocalTime.of(15, 30))
            .to(LocalTime.of(17, 0))
            .subject("Subject")
            .description("Description")
            .interviewerTimeSlotId(interviewerTimeSlotRepoTest.findAll().get(0).getId())
            .candidateTimeSlotId(candidateTimeSlotRepoTest.findAll().get(0).getId())
            .build());

    ResponseEntity<Booking> response =
        restTemplate.postForEntity(baseUrl + "/bookings", request, Booking.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, bookingRepo.findAll().size());
  }
}


