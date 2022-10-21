package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;


/**
 * Class to describe a limit of bookings for interviewer.
 */
@Entity
//@Data
@Getter
@Setter
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
  @Column(name = "week_booking_limit")
  private Integer weekBookingLimit = 0;
  @Column(name = "current_booking_count")
  private Integer currentBookingCount = 0;
  @Column(name = "interviewer_id")
  private UUID interviewerId;

  /**
   * Constructor.
   *
   * @param weekBookingLimit    interviewer booking limit per week
   * @param currentBookingCount used to check if limit is not exceeded
   */

  public InterviewerBookingLimit(Integer weekBookingLimit,
                                 Integer currentBookingCount) {
    this.weekBookingLimit = weekBookingLimit;
    this.currentBookingCount = currentBookingCount;
  }

}
