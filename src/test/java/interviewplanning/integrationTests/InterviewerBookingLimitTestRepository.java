package interviewplanning.integrationTests;

import interviewplanning.models.InterviewerBookingLimit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerBookingLimitTestRepository
    extends JpaRepository<InterviewerBookingLimit, UUID> {
  List<InterviewerBookingLimit> findByInterviewerId(UUID id);

  InterviewerBookingLimit findByInterviewerIdAndWeekNum(UUID interviewerId,
                                                        String weekNum);
}
