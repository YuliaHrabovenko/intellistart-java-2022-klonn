package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for InterviewerTimeSlot entity.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, Long> {
}
