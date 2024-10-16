package interviewplanning.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import interviewplanning.models.User;
import interviewplanning.models.UserRole;
import interviewplanning.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepoTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private UserRepository userRepository;

  @Test
  public void shouldFindNoUsersIfRepositoryIsEmpty() {
    Iterable<User> users = userRepository.findAll();
    assertThat(users).isEmpty();
  }

  @Test
  public void shouldSaveUser() {
    User user = userRepository.save(new User("interviewer@gmail.com", UserRole.INTERVIEWER));
    assertThat(userRepository.findAll()).hasSize(1);
  }

  @Test
  public void shouldFindAllUsers() {
    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User user3 = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persist(user3);

    Iterable<User> tutorials = userRepository.findAll();

    assertThat(tutorials).hasSize(2).contains(user2, user3);
  }

  @Test
  public void shouldFindUserById() {
    User user1 = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persist(user1);

    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User foundUser = userRepository.findById(user2.getId()).get();

    assertThat(foundUser).isEqualTo(user2);
  }

  @Test
  public void shouldUpdateUserById() {
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
    User user2 = new User("coordinator@gmail.com", UserRole.COORDINATOR);
    entityManager.persist(user2);

    User user3 = new User("interviewer@gmail.com", UserRole.INTERVIEWER);
    entityManager.persist(user3);

    userRepository.deleteById(user2.getId());

    Iterable<User> users = userRepository.findAll();

    assertThat(users).hasSize(1).contains(user3);
  }

  @Test
  public void shouldDeleteAllUsers() {
    entityManager.persist(new User("interviewer@gmail.com", UserRole.INTERVIEWER));
    entityManager.persist(new User("coordinator@gmail.com", UserRole.COORDINATOR));

    userRepository.deleteAll();

    assertThat(userRepository.findAll()).isEmpty();
  }

}
