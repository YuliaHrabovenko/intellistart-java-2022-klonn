package com.intellias.intellistart.interviewplanning.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Class to describe a limit of bookings for interviewer.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "interviewer_booking_limits")
public class InterviewerBookingLimit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "uuid")
  private UUID uuid = UUID.randomUUID();
  @Column(name = "week_number")
  private Long weekNumber;
  @Column(name = "week_booking_limit")
  private Integer weekBookingLimit;
  @Column(name = "current_booking_count")
  private Integer currentBookingCount;
  @Column(name = "interviewer_id")
  private Long interviewerId;

  /**
   * Constructor.
   *
   * @param weekNumber          week number
   * @param weekBookingLimit    interviewer booking limit per week
   * @param currentBookingCount used to check if limit is not exceeded
   */

  public InterviewerBookingLimit(Long weekNumber, Integer weekBookingLimit,
                                 Integer currentBookingCount) {
    this.weekNumber = weekNumber;
    this.weekBookingLimit = weekBookingLimit;
    this.currentBookingCount = currentBookingCount;
  }

}
