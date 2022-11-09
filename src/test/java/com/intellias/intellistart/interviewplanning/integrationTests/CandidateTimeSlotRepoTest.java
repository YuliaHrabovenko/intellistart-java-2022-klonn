package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateTimeSlotRepoTest extends JpaRepository<CandidateTimeSlot, UUID> {

}
