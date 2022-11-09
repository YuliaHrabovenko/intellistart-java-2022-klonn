package com.intellias.intellistart.interviewplanning.integrationTests.repos;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerTimeSlotTestRepository
    extends JpaRepository<InterviewerTimeSlot, UUID> {
  List<InterviewerTimeSlot> findInterviewerTimeSlotsByInterviewerIdAndWeekNum(UUID interviewerId,
                                                                              String weekNum);
}
