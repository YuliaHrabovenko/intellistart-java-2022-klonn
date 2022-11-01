package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @NotNull(message = "from has to be present")
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "start_time")
  private LocalTime from;
  @NotNull(message = "to has to be present")
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "end_time")
  private LocalTime to;
  @NotNull(message = "subject has to be present")
  @NotBlank
  @Size(max = 255, message = "Subject's length can't be higher that 255 characters")
  @Column(name = "subject")
  private String subject;
  @NotNull(message = "description has to be present")
  @NotBlank
  @Size(max = 4000, message = "Description's length can't be higher that 4000 characters")
  @Column(name = "description")
  private String description;
  @NotNull(message = "interviewerTimeSlotId has to be present")
  @Column(name = "interviewer_time_slot_id")
  private UUID interviewerTimeSlotId;
  @NotNull(message = "candidateTimeSlotId has to be present")
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
