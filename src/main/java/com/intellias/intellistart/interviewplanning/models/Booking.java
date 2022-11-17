package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 * Booking model.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
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
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "start_time", nullable = false)
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "end_time", nullable = false)
  private LocalTime to;
  @Column(name = "subject", length = 255)
  private String subject;
  @Column(name = "description", length = 4000)
  private String description;
  @JsonProperty("interviewer_time_slot_id")
  @Column(name = "interviewer_time_slot_id", nullable = false)
  private UUID interviewerTimeSlotId;
  @JsonProperty("candidate_time_slot_id")
  @Column(name = "candidate_time_slot_id", nullable = false)
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
