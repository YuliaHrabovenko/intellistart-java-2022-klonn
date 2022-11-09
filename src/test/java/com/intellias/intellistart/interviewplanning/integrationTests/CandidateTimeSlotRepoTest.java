package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateTimeSlotRepoTest extends JpaRepository<CandidateTimeSlot, UUID> {
  List<CandidateTimeSlot> findCandidateTimeSlotsByDateBetween(LocalDate from, LocalDate to);
}
