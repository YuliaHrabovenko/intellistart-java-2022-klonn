package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerBookingLimit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for InterviewerBookingLimit entity.
 */
@Repository
public interface InterviewerBookingLimitRepository extends
    JpaRepository<InterviewerBookingLimit, UUID> {

  Optional<InterviewerBookingLimit> findByInterviewerId(UUID id);

  @Query(value = "select * from interviewer_booking_limits ibl where ibl.interviewer_id = ?1",
      nativeQuery = true)
  List<InterviewerBookingLimit> getInterviewerBookingLimitsByInterviewerId(UUID id);

  @Query(value = "select * from interviewer_booking_limits ibl where ibl.interviewer_id = ?1 and ibl.week_id = ?2",
      nativeQuery = true)
  InterviewerBookingLimit getBookingLimitByInterviewerIdAndWeekId(UUID interviewerId, UUID weekId);

}
