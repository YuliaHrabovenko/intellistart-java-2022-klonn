package com.intellias.intellistart.interviewplanning.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.integrationTests.repos.InterviewerBookingLimitTestRepository;
import com.intellias.intellistart.interviewplanning.integrationTests.repos.InterviewerTimeSlotTestRepository;
import com.intellias.intellistart.interviewplanning.integrationTests.repos.UserTestRepository;
import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import java.time.DayOfWeek;
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
public class InterviewerIntegrationTests {

  @LocalServerPort
  private int port;

  private String baseUrl = "http://localhost:";

  private static RestTemplate restTemplate;

  @Autowired
  private UserTestRepository userTestRepository;

  @Autowired
  private InterviewerBookingLimitTestRepository interviewerBookingLimitTestRepository;

  @Autowired
  private InterviewerTimeSlotTestRepository interviewerTimeSlotTestRepository;

  @BeforeAll
  public static void init() {
    restTemplate = new RestTemplate();
  }

  @BeforeEach
  public void setUp() {
    baseUrl = baseUrl.concat(port + "");
    interviewerBookingLimitTestRepository.deleteAll();
    interviewerTimeSlotTestRepository.deleteAll();
    userTestRepository.deleteAll();
  }

  @Test
  void testGetBookingLimitByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<List<InterviewerBookingLimit>> entity = new HttpEntity<>(
        interviewerBookingLimitTestRepository.findByInterviewerId(interviewerID));

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/interviewers/booking-limits/" + interviewerID,  HttpMethod.GET, entity, List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testCreateNextWeekInterviewerBookingLimitSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<InterviewerBookingLimit> entity = new HttpEntity<>(
        InterviewerBookingLimit.builder()
            .interviewerId(interviewerID)
            .weekNum(WeekUtil.getNextWeekNumber())
            .weekBookingLimit(5)
            .currentBookingCount(0)
            .build());

    ResponseEntity<InterviewerBookingLimit> response = restTemplate.exchange(
        baseUrl + "/interviewers/booking-limits",  HttpMethod.POST, entity, InterviewerBookingLimit.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }


  @Test
  void testCreateInterviewerTimeSlotSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<InterviewerTimeSlot> entity = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
            .from(LocalTime.of(15, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekNum(WeekUtil.getNextWeekNumber())
            .build());

    ResponseEntity<InterviewerTimeSlot> response = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/slots",  HttpMethod.POST, entity, InterviewerTimeSlot.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }


  @Test
  void testUpdateInterviewerTimeSlotSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<InterviewerTimeSlot> entityCreate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekNum(WeekUtil.getNextWeekNumber())
            .build());

    ResponseEntity<InterviewerTimeSlot> responseCreate = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/slots",  HttpMethod.POST, entityCreate, InterviewerTimeSlot.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, responseCreate.getStatusCode());

    HttpEntity<InterviewerTimeSlot> entityUpdate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))
            .from(LocalTime.of(14, 30))
            .to(LocalTime.of(16, 0))
            .interviewerId(interviewer.getId())
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekNum(WeekUtil.getNextWeekNumber())
            .build());

    ResponseEntity<InterviewerTimeSlot> responseUpdate = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/next-week-slots/" +
            interviewerTimeSlotTestRepository.findAll().get(0).getId(),  HttpMethod.POST,
        entityUpdate, InterviewerTimeSlot.class);

    assertEquals(HttpStatus.OK, responseUpdate.getStatusCode());

  }


  @Test
  void testGetCurrentWeekTimeSlotsByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<List<InterviewerTimeSlot>> entity = new HttpEntity<>(
        interviewerTimeSlotTestRepository.findInterviewerTimeSlotsByInterviewerIdAndWeekNum(
            interviewerID, WeekUtil.getCurrentWeekNumber()));

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/weeks/current/interviewers/" + interviewerID + "/slots",  HttpMethod.GET, entity, List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  @Test
  void testGetNextWeekTimeSlotsByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findUserByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<List<InterviewerTimeSlot>> entity = new HttpEntity<>(
        interviewerTimeSlotTestRepository.findInterviewerTimeSlotsByInterviewerIdAndWeekNum(
            interviewerID, WeekUtil.getNextWeekNumber()));

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/weeks/next/interviewers/" + interviewerID + "/slots",  HttpMethod.GET, entity,
        List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}
