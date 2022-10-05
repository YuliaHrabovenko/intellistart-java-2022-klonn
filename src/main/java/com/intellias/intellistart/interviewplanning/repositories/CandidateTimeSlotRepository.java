package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {
}
