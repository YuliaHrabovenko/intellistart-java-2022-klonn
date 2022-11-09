package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewerTimeSlotRepoTest extends JpaRepository<InterviewerTimeSlot, UUID> {

}
