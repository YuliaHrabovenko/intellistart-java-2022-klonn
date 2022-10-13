package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
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
 * InterviewerTimeSlot model.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "interviewer_time_slots")
public class InterviewerTimeSlot {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "day_of_week")
  private DayOfWeek dayOfWeek;
  @Column(name = "period_id")
  private UUID periodId;
  @Column(name = "interviewer_id")
  private UUID interviewerId;

  /**
   * Constructor.
   *
   * @param dayOfWeek     day of week
   * @param periodId      period id
   * @param interviewerId interviewer id
   */

  public InterviewerTimeSlot(DayOfWeek dayOfWeek, UUID periodId, UUID interviewerId) {
    this.dayOfWeek = dayOfWeek;
    this.periodId = periodId;
    this.interviewerId = interviewerId;
  }
}
