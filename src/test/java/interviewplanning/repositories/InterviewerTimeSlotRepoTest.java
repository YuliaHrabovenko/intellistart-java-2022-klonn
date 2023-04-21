package interviewplanning.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import interviewplanning.models.InterviewerTimeSlot;
import interviewplanning.models.User;
import interviewplanning.models.UserRole;
import interviewplanning.utils.WeekUtil;
import interviewplanning.repositories.InterviewerTimeSlotRepository;
import interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InterviewerTimeSlotRepoTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Autowired
  private UserRepository userRepository;
  private User interviewer;
  private InterviewerTimeSlot slot;

  @Before
  public void setup() {
    interviewer = new User("user@gmail.com", UserRole.INTERVIEWER);
    entityManager.persistAndFlush(interviewer);
    slot = InterviewerTimeSlot.builder()
        .interviewerId(interviewer.getId())
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(WeekUtil.getNextWeekNumber())
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .build();
    entityManager.persistAndFlush(slot);
  }

  @Test
  public void shouldFindSlotByInterviewerIdAndWeekNum() {
    List<InterviewerTimeSlot> foundSlots =
        interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(interviewer.getId(),
            WeekUtil.getNextWeekNumber());

    assertThat(foundSlots).hasSize(1).contains(slot);
  }

  @Test
  public void shouldFindSlotByDayOfWeekAndInterviewerIdAndWeekNum() {
    List<InterviewerTimeSlot> foundSlots =
        interviewerTimeSlotRepository.findByDayOfWeekAndInterviewerIdAndWeekNum(DayOfWeek.MONDAY,
            interviewer.getId(), WeekUtil.getNextWeekNumber());

    assertThat(foundSlots).hasSize(1).contains(slot);
  }

  @Test
  public void shouldFindSlotByWeekNum() {

    List<InterviewerTimeSlot> foundSlots =
        interviewerTimeSlotRepository.findByWeekNum(WeekUtil.getNextWeekNumber());

    assertThat(foundSlots).hasSize(1).contains(slot);
  }

}
