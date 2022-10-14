package com.intellias.intellistart.interviewplanning.models;

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
 * Class to describe a limit of bookings for interviewer.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "interviewer_booking_limits")
public class InterviewerBookingLimit {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "week_number")
  private Long weekNumber;
  @Column(name = "week_booking_limit")
  private Integer weekBookingLimit;
  @Column(name = "current_booking_count")
  private Integer currentBookingCount;
  @Column(name = "interviewer_id")
  private UUID interviewerId;

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
