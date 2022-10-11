package com.intellias.intellistart.interviewplanning.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CandidateTimeSlot model.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "candidate_time_slots")
public class CandidateTimeSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "uuid")
  private UUID uuid = UUID.randomUUID();
  @Column(name = "period_id")
  private Long periodId;
  @Column(name = "candidate_id")
  private Long candidateId;

  public CandidateTimeSlot(Long periodId, Long candidateId) {
    this.periodId = periodId;
    this.candidateId = candidateId;
  }

}
