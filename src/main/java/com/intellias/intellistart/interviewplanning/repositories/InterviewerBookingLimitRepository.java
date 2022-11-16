package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for InterviewerBookingLimit entity.
 */
@Repository
public interface InterviewerBookingLimitRepository extends
    JpaRepository<InterviewerBookingLimit, UUID> {

  List<InterviewerBookingLimit> findByInterviewerId(UUID id);

  InterviewerBookingLimit findByInterviewerIdAndWeekNum(UUID interviewerId, String weekNum);
}
