package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for InterviewerBookingLimit entity.
 */
@Repository
public interface InterviewerBookingLimitRepository extends
    JpaRepository<InterviewerBookingLimit, UUID> {

  Optional<InterviewerBookingLimit> findByInterviewerId(UUID id);

  List<InterviewerBookingLimit> findInterviewerBookingLimitsByInterviewerId(UUID id);

  InterviewerBookingLimit findInterviewerBookingLimitByInterviewerIdAndWeekNum(UUID interviewerId,
                                                                               String weekNum);
}
