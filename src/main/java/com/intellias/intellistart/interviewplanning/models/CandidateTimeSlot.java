package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * CandidateTimeSlot model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CandidateTimeSlot {
  @Id
  private Long id;
  private LocalDateTime start;
  private LocalTime duration;
  @ManyToOne
  private Candidate candidate;

  /**
   * Constructor.
   *
   * @param id       id of a candidate slot in the db
   * @param start    exact date
   * @param duration time diapason
   */
  public CandidateTimeSlot(long id, LocalDateTime start, LocalTime duration) {
    this.id = id;
    this.start = start;
    this.duration = duration;
  }
}
