package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "start_time")
  private LocalTime from;
  @Column(name = "end_time")
  private LocalTime to;
  @Column(name = "subject")
  private String subject;
  @Column(name = "description")
  private String description;
  @Column(name = "interviewer_time_slot_id")
  private UUID interviewerTimeSlotId;
  @Column(name = "candidate_time_slot_id")
  private UUID candidateTimeSlotId;

  /**
   * Constructor.
   *
   * @param from                  start time
   * @param to                    end time
   * @param subject               booking subject
   * @param description           booking description
   * @param interviewerTimeSlotId interviewer time slot id
   * @param candidateTimeSlotId   candidate time slot id
   */

  public Booking(LocalTime from, LocalTime to,
                 UUID interviewerTimeSlotId, UUID candidateTimeSlotId,
                 String subject, String description) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.description = description;
    this.interviewerTimeSlotId = interviewerTimeSlotId;
    this.candidateTimeSlotId = candidateTimeSlotId;
  }
}
