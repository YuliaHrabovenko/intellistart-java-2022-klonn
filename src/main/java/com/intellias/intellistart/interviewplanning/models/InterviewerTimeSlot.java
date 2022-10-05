package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * InterviewerTimeSlot model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InterviewerTimeSlot {
  @Id
  private Long id;
  private DayOfWeek dayOfWeek;
  private Integer weekNum;
  private LocalTime from;
  private LocalTime to;
  @ManyToOne
  private Interviewer interviewer;

  /**
   * Constructor.
   *
   * @param id        the id of interviewer`s time slot in db
   * @param dayOfWeek the day of week
   * @param from      start time
   * @param to        end time
   */
  public InterviewerTimeSlot(long id,
                             DayOfWeek dayOfWeek,
                             LocalTime from,
                             LocalTime to) {
    this.id = id;
    this.dayOfWeek = dayOfWeek;
    this.from = from;
    this.to = to;
  }
}
