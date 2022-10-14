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
import org.hibernate.annotations.GenericGenerator;

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
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "period_id")
  private UUID periodId;
  @Column(name = "candidate_id")
  private UUID candidateId;

  public CandidateTimeSlot(UUID periodId, UUID candidateId) {
    this.periodId = periodId;
    this.candidateId = candidateId;
  }

}
