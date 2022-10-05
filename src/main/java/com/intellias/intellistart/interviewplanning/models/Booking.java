package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Booking model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Booking {
  @Id
  private Long id;
  private LocalTime from;
  private LocalTime to;
  private String subject;
  private String description;
  private BookingStatus status;
  @ManyToOne
  private InterviewerTimeSlot interviewerSlot;
  @ManyToOne
  private CandidateTimeSlot candidateSlot;

  /**
   * Constructor.
   *
   * @param interviewerSlot interviewer slot
   * @param candidateSlot   candidate slot
   * @param from            start time of booking
   * @param to              end time of booking
   * @param subject         booking subject
   * @param description     booking description
   */
  public Booking(InterviewerTimeSlot interviewerSlot,
                 CandidateTimeSlot candidateSlot,
                 LocalTime from,
                 LocalTime to,
                 String subject,
                 String description) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.description = description;
    this.interviewerSlot = interviewerSlot;
    this.candidateSlot = candidateSlot;
  }
}
