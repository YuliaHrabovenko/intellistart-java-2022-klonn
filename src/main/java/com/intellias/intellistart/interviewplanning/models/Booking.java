package com.intellias.intellistart.interviewplanning.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking model.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "uuid")
  private UUID uuid = UUID.randomUUID();
  @Column(name = "subject")
  private String subject;
  @Column(name = "description")
  private String description;
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private BookingStatus status;
  @Column(name = "period_id")
  private Long periodId;
  @Column(name = "interviewer_time_slot_id")
  private Long interviewerTimeSlotId;
  @Column(name = "candidate_time_slot_id")
  private Long candidateTimeSlotId;

  /**
   * Constructor.
   *
   * @param periodId              id of period
   * @param interviewerTimeSlotId interviewer time slot id
   * @param candidateTimeSlotId   candidate time slot id
   * @param status                status of booking
   * @param subject               booking subject
   * @param description           booking description
   */

  public Booking(Long periodId,
                 Long interviewerTimeSlotId,
                 Long candidateTimeSlotId,
                 BookingStatus status,
                 String subject,
                 String description) {
    this.periodId = periodId;
    this.interviewerTimeSlotId = interviewerTimeSlotId;
    this.candidateTimeSlotId = candidateTimeSlotId;
    this.status = status;
    this.subject = subject;
    this.description = description;
  }

}
