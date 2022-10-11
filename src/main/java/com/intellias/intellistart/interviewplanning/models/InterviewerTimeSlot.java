package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "uuid")
  private UUID uuid = UUID.randomUUID();
  @Column(name = "day_of_week")
  private DayOfWeek dayOfWeek;
  @Column(name = "period_id")
  private Long periodId;
  @Column(name = "interviewer_id")
  private Long interviewerId;

  /**
   * Constructor.
   *
   * @param dayOfWeek     day of week
   * @param periodId      period id
   * @param interviewerId interviewer id
   */

  public InterviewerTimeSlot(DayOfWeek dayOfWeek, Long periodId, Long interviewerId) {
    this.dayOfWeek = dayOfWeek;
    this.periodId = periodId;
    this.interviewerId = interviewerId;
  }
}
