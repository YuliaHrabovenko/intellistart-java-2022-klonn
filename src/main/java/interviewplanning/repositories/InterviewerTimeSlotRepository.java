package interviewplanning.repositories;

import interviewplanning.models.InterviewerTimeSlot;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for InterviewerTimeSlot entity.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, UUID> {
  List<InterviewerTimeSlot> findByInterviewerIdAndWeekNum(UUID interviewerId, String weekNum);

  List<InterviewerTimeSlot> findByDayOfWeekAndInterviewerIdAndWeekNum(DayOfWeek dayOfWeek,
                                                                      UUID interviewerId,
                                                                      String weekNum);

  List<InterviewerTimeSlot> findByWeekNum(String weekNum);
}
