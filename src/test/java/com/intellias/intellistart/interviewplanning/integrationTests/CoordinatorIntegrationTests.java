package com.intellias.intellistart.interviewplanning.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
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
}
