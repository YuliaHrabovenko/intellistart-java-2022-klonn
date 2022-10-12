package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
  @Autowired
  TestEntityManager entityManager;
  @Autowired UserRepository userRepository;

  @Test
  public void shouldFindNoUsersIfRepositoryIsEmpty() {
    Iterable users = userRepository.findAll();

    assertThat(users).isEmpty();
  }

  @Test
  public void shouldSaveUser() {
    User user = userRepository.save(new User("candidate1@gmail.com", UserRole.CANDIDATE));

    assertThat(userRepository.findAll()).hasSize(1);
//    assertThat(user).hasFieldOrPropertyWithValue("role", UserRole.CANDIDATE);
  }

  @Test
  public void shouldFindAllUsers() {
    User user1 = new User("candidate@gmail.com", UserRole.CANDIDATE);
    entityManager.persist(user1);

    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User user3 = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persist(user3);

    Iterable<User> tutorials = userRepository.findAll();

    assertThat(tutorials).hasSize(3).contains(user1, user2, user3);
  }

  @Test
  public void shouldFindUserById() {
    User user1 = new User("candidate@gmail.com", UserRole.CANDIDATE);
    entityManager.persist(user1);

    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User foundUser = userRepository.findById(user2.getId()).get();

    assertThat(foundUser).isEqualTo(user2);
  }

  @Test
  public void shouldUpdateUserById() {
    User user1 = new User("candidate@gmail.com", UserRole.CANDIDATE);
    entityManager.persist(user1);

    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User updatedUser = new User("candidate_test@gmail.com", UserRole.INTERVIEWER);

    User user = userRepository.findById(user2.getId()).get();
    user.setEmail(updatedUser.getEmail());
    user.setRole(updatedUser.getRole());
    userRepository.save(user);

    User checkUser = userRepository.findById(user2.getId()).get();

    assertThat(checkUser.getId()).isEqualTo(user2.getId());
    assertThat(checkUser.getEmail()).isEqualTo(updatedUser.getEmail());
    assertThat(checkUser.getRole()).isEqualTo(updatedUser.getRole());
  }

  @Test
  public void shouldDeleteUserById() {
    User user1 = new User("candidate@gmail.com", UserRole.CANDIDATE);
    entityManager.persist(user1);

    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User user3 = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persist(user3);

    userRepository.deleteById(user2.getId());

    Iterable<User> users = userRepository.findAll();

    assertThat(users).hasSize(2).contains(user1, user3);
  }

  @Test
  public void shouldDeleteAllUsers() {
    entityManager.persist(new User("candidate@gmail.com", UserRole.CANDIDATE));
    entityManager.persist(new User("coordinator@gmail.com", UserRole.COORDINATOR));

    userRepository.deleteAll();

    assertThat(userRepository.findAll()).isEmpty();
  }

}
