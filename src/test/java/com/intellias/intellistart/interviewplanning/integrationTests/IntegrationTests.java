package com.intellias.intellistart.interviewplanning.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.security.FacebookToken;
import com.intellias.intellistart.interviewplanning.security.JwtTokenProvider;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

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

  @Autowired
  private CandidateTimeSlotRepoTest candidateTimeSlotRepoTest;

  @Autowired
  private BookingRepo bookingRepo;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private FacebookToken facebookToken;

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.token.expired}")
  private int expirationTime;

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

  public HttpHeaders getHeaders(String email, UserRole role){
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("name", "John Doe");
    claims.put("role", role);
    Date now = new Date();
    Date validity = new Date(now.getTime() + expirationTime);
    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return headers;
  }

  @Test
  void testGetBookingLimitByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<List<InterviewerBookingLimit>> entity = new HttpEntity<>(
        interviewerBookingLimitTestRepository.findByInterviewerId(interviewerID), headers);

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID +"/booking-limits", HttpMethod.GET, entity,
        List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testCreateNextWeekInterviewerBookingLimitSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<InterviewerBookingLimit> entity = new HttpEntity<>(
        InterviewerBookingLimit.builder()
            .interviewerId(interviewerID)
            .weekNum(WeekUtil.getNextWeekNumber())
            .weekBookingLimit(5)
            .currentBookingCount(0)
            .build(), headers);

    ResponseEntity<InterviewerBookingLimit> response = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID +"/booking-limits", HttpMethod.POST, entity,
        InterviewerBookingLimit.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }


  @Test
  void testCreateInterviewerTimeSlotSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<InterviewerTimeSlot> entity = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
            .from(LocalTime.of(15, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekNum(WeekUtil.getNextWeekNumber())
            .build(), headers);

    ResponseEntity<InterviewerTimeSlot> response = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/slots", HttpMethod.POST, entity,
        InterviewerTimeSlot.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }


  @Test
  void testUpdateInterviewerTimeSlotByCoordinatorSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<InterviewerTimeSlot> entityCreate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .id(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .dayOfWeek(DayOfWeek.MONDAY)
            .weekNum(WeekUtil.getNextWeekNumber())
            .build(), headers);

    ResponseEntity<InterviewerTimeSlot> responseCreate = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/slots", HttpMethod.POST, entityCreate,
        InterviewerTimeSlot.class);

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
            .build(), headers);

    ResponseEntity<InterviewerTimeSlot> responseUpdate = restTemplate.exchange(
        baseUrl + "/interviewers/" + interviewerID + "/next-week-slots/" +
            interviewerTimeSlotTestRepository.findAll().get(0).getId(), HttpMethod.POST,
        entityUpdate, InterviewerTimeSlot.class);

    assertEquals(HttpStatus.OK, responseUpdate.getStatusCode());

  }


  @Test
  void testGetCurrentWeekTimeSlotsByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<List<InterviewerTimeSlot>> entity = new HttpEntity<>(
        interviewerTimeSlotTestRepository.findByInterviewerIdAndWeekNum(
            interviewerID, WeekUtil.getCurrentWeekNumber()), headers);

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/weeks/current/interviewers/" + interviewerID + "/slots", HttpMethod.GET, entity,
        List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  @Test
  void testGetNextWeekTimeSlotsByInterviewerIdSuccess() {
    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    HttpEntity<List<InterviewerTimeSlot>> entity = new HttpEntity<>(
        interviewerTimeSlotTestRepository.findByInterviewerIdAndWeekNum(
            interviewerID, WeekUtil.getNextWeekNumber()), headers);

    ResponseEntity<List> response = restTemplate.exchange(
        baseUrl + "/weeks/next/interviewers/" + interviewerID + "/slots", HttpMethod.GET, entity,
        List.class);

    assertEquals(1, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }


  // Coordinator tests
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

    HttpHeaders headers = getHeaders("FirstCoordinator@gmail.com", UserRole.COORDINATOR);
    HttpEntity<List<User>> entity = new HttpEntity<>(userTestRepository.findAll(), headers);

    ResponseEntity<List> response = restTemplate.exchange(baseUrl + "/users/interviewers", HttpMethod.GET, entity, List.class);
    assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    assertEquals(4, userTestRepository.findAll().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
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

    HttpHeaders headers = getHeaders("FirstCoordinator@gmail.com", UserRole.COORDINATOR);
    HttpEntity<List<User>> entity = new HttpEntity<>(userTestRepository.findAll(), headers);

    ResponseEntity<List> response = restTemplate.exchange(baseUrl + "/users/coordinators", HttpMethod.GET, entity, List.class);
    assertEquals(4, userTestRepository.findAll().size());
    assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testCreateInterviewerByEmailSuccess() {
    User coordinator1 = new User("existing_coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);

    HttpHeaders headers = getHeaders("existing_coordinator@gmail.com", UserRole.COORDINATOR);

    baseUrl = baseUrl.concat("/users/interviewers");
    HttpEntity<User> request = new HttpEntity<>(new User("FirstInterviewer@gmail.com"), headers);
    ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2, userTestRepository.findAll().size());
  }

  @Test
  void testCreateCoordinatorByEmailSuccess() {
    User coordinator1 = new User("existing_coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);

    HttpHeaders headers = getHeaders("existing_coordinator@gmail.com", UserRole.COORDINATOR);

    baseUrl = baseUrl.concat("/users/coordinators");
    HttpEntity<User> request = new HttpEntity<>(new User("FirstCoordinator@gmail.com"), headers);
    ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, request, User.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2, userTestRepository.findAll().size());
  }

  @Test
  void testDeleteInterviewerByIdSuccess() {

    User coordinator1 = new User("existing_coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);

    HttpHeaders headers = getHeaders("existing_coordinator@gmail.com", UserRole.COORDINATOR);

    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);
    assertEquals(2, userTestRepository.findAll().size());

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<User> request = new HttpEntity<>(headers);
    restTemplate.exchange(baseUrl + "/users/interviewers/" + interviewerID, HttpMethod.DELETE, request, String.class);

    assertEquals(1, userTestRepository.findAll().size());
  }

  @Test
  void testDeleteCoordinatorByIdSuccess() {
    User coordinator1 = new User("existing_coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator1);

    HttpHeaders headers = getHeaders("existing_coordinator@gmail.com", UserRole.COORDINATOR);

    User coordinator = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    userTestRepository.save(coordinator);
    assertEquals(2, userTestRepository.findAll().size());

    UUID coordinatorID = userTestRepository.findByEmail(coordinator.getEmail()).get().getId();
    HttpEntity<User> request = new HttpEntity<>(headers);
    restTemplate.exchange(baseUrl + "/users/coordinators/" + coordinatorID, HttpMethod.DELETE, request, String.class);

    assertEquals(1, userTestRepository.findAll().size());
  }

  @Test
  void testUpdateInterviewerTimeSlotSuccess() {
    HttpHeaders headers = getHeaders("interviewer@gmail.com", UserRole.INTERVIEWER);

    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    userTestRepository.save(interviewer);

    UUID interviewerID = userTestRepository.findByEmail(interviewer.getEmail()).get().getId();

    HttpEntity<InterviewerTimeSlot> requestCreate = new HttpEntity<>(
        InterviewerTimeSlot.builder()
            .weekNum(WeekUtil.getNextWeekNumber())
            .dayOfWeek(DayOfWeek.FRIDAY)
            .from(LocalTime.of(13, 30))
            .to(LocalTime.of(17, 0))
            .interviewerId(interviewer.getId())
            .build(), headers);

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
            .build(), headers);

    String URLToUpdate = baseUrl + "/interviewers/" + interviewerID + "/next-week-slots/" +
        interviewerTimeSlotTestRepository.findAll().get(0).getId();


    ResponseEntity<InterviewerTimeSlot> responseUpdate = restTemplate.exchange(
        URLToUpdate, HttpMethod.POST, requestUpdate, InterviewerTimeSlot.class);

    assertEquals(HttpStatus.OK, responseUpdate.getStatusCode());

  }

//  @Test
//  void testCreateBookingSuccess() {
//    User coordinator1 = new User("existing_coordinator@gmail.com", UserRole.COORDINATOR);
//    userTestRepository.save(coordinator1);
//
//    HttpHeaders headers = getHeaders("existing_coordinator@gmail.com", UserRole.COORDINATOR);
//
//    User interviewer = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
//    userTestRepository.save(interviewer);
//
//    User candidate = new User("candidate@gmail.com");
//    userTestRepository.save(candidate);
//
//    InterviewerTimeSlot interviewerTimeSlot =
//        InterviewerTimeSlot.builder()
//            .weekNum(WeekUtil.getNextWeekNumber())
//            .dayOfWeek(DayOfWeek.THURSDAY)
//            .from(LocalTime.of(13, 30))
//            .to(LocalTime.of(17, 0))
//            .interviewerId(interviewer.getId())
//            .build();
//
//    interviewerTimeSlotTestRepository.save(interviewerTimeSlot);
//    assertEquals(1, interviewerTimeSlotTestRepository.findAll().size());
//
//    CandidateTimeSlot candidateTimeSlot =
//        CandidateTimeSlot.builder()
//            .date(LocalDate.now())
//            .from(LocalTime.of(13, 30))
//            .to(LocalTime.of(17, 0))
//            .build();
//
//    candidateTimeSlotRepoTest.save(candidateTimeSlot);
//    assertEquals(1, candidateTimeSlotRepoTest.findAll().size());
//
//    HttpEntity<Booking> request = new HttpEntity<>(
//        Booking.builder()
//            .from(LocalTime.of(15, 30))
//            .to(LocalTime.of(17, 0))
//            .interviewerTimeSlotId(interviewerTimeSlotTestRepository.findAll().get(0).getId())
//            .candidateTimeSlotId(candidateTimeSlotRepoTest.findAll().get(0).getId())
//            .subject("Subject")
//            .description("Description")
//            .build(), headers);
//
//    ResponseEntity<Booking> response =
//        restTemplate.postForEntity(baseUrl + "/bookings", request, Booking.class);
//
//    assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    assertEquals(1, bookingRepo.findAll().size());
//  }

}
