package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, UUID> {
  @Query(value = "select * from candidate_time_slots cts where cts.candidate_id = ?1",
      nativeQuery = true)
  List<CandidateTimeSlot> getCandidateSlotsByCandidateId(UUID candidateId);
}
