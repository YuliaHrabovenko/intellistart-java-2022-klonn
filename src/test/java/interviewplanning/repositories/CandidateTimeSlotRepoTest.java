package interviewplanning.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import interviewplanning.models.CandidateTimeSlot;
import interviewplanning.repositories.CandidateTimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CandidateTimeSlotRepoTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private CandidateTimeSlotRepository candidateTimeSlotRepo;

  private CandidateTimeSlot slot;

  @Before
  public void setup() {
    slot = CandidateTimeSlot.builder()
        .date(LocalDate.now())
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(10, 30))
        .email("candidate@gmail.com")
        .build();
    entityManager.persistAndFlush(slot);
  }

  @Test
  public void shouldFindSlotsByDateBetween() {
    CandidateTimeSlot slot1 = CandidateTimeSlot.builder()
        .date(LocalDate.now().plusDays(3))
        .from(LocalTime.of(11, 0))
        .to(LocalTime.of(12, 30))
        .email("test@gmail.com")
        .build();
    entityManager.persistAndFlush(slot1);

    LocalDate dateFrom = LocalDate.now().minusDays(1L);
    LocalDate dateTo = LocalDate.now().plusDays(4L);

    List<CandidateTimeSlot> slots = candidateTimeSlotRepo.findByDateBetween(dateFrom, dateTo);
    assertThat(slots).hasSize(2).containsAll(List.of(slot, slot1));
  }

  @Test
  public void shouldFindSlotsByEmail() {
    CandidateTimeSlot slot1 = CandidateTimeSlot.builder()
        .date(LocalDate.now().plusDays(10))
        .from(LocalTime.of(11, 0))
        .to(LocalTime.of(12, 30))
        .email("candidate@gmail.com")
        .build();
    entityManager.persistAndFlush(slot1);

    List<CandidateTimeSlot> slots = candidateTimeSlotRepo.findByEmail("candidate@gmail.com");
    assertThat(slots).hasSize(2).containsAll(List.of(slot, slot1));
  }

  @Test
  public void shouldFindSlotsByEmailAndDate() {
    List<CandidateTimeSlot> slots =
        candidateTimeSlotRepo.findByEmailAndDate("candidate@gmail.com", LocalDate.now());
    assertThat(slots).hasSize(1).contains(slot);
  }

}
