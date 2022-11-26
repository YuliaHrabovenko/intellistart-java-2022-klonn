package com.intellias.intellistart.interviewplanning.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.utils.WeekUtil;
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
public class InterviewerBookingLimitRepoTest {
  @Autowired
  private TestEntityManager entityManager;
  @Autowired
  private InterviewerBookingLimitRepository interviewerBookingLimitRepo;

  private User interviewer;
  private InterviewerBookingLimit limit;

  @Before
  public void setup() {
    interviewer = new User("user@gmail.com", UserRole.INTERVIEWER);
    entityManager.persistAndFlush(interviewer);
    limit = InterviewerBookingLimit.builder()
        .weekNum(WeekUtil.getNextWeekNumber())
        .interviewerId(interviewer.getId())
        .weekBookingLimit(10)
        .build();
    entityManager.persistAndFlush(limit);
  }

  @Test
  public void shouldFindByInterviewerId() {
    InterviewerBookingLimit limit1 = InterviewerBookingLimit.builder()
        .weekNum(WeekUtil.getCurrentWeekNumber())
        .interviewerId(interviewer.getId())
        .weekBookingLimit(10)
        .build();
    entityManager.persistAndFlush(limit1);

    List<InterviewerBookingLimit> limits =
        interviewerBookingLimitRepo.findByInterviewerId(interviewer.getId());
    assertThat(limits).containsAll(List.of(limit, limit1));
  }

  @Test
  public void shouldFindByInterviewerIdAndWeekNum() {
    InterviewerBookingLimit expectedLimit =
        interviewerBookingLimitRepo.findByInterviewerIdAndWeekNum(interviewer.getId(),
            WeekUtil.getNextWeekNumber());
    assertThat(expectedLimit).isEqualTo(limit);
  }

}
