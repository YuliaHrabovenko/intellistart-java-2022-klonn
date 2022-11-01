package com.intellias.intellistart.interviewplanning.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


/**
 * Class to describe a limit of bookings for interviewer.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
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
  @NotNull(message = "weekBookingLimit has to be present")
  @Min(value = 1, message = "Week booking limit must be greater or equal to {value}")
  @Column(name = "week_booking_limit")
  private Integer weekBookingLimit;
  @Min(value = 0, message = "Current booking limit must be greater than {value}")
  @Column(name = "current_booking_count")
  private Integer currentBookingCount = 0;
  @NotNull(message = "weekNum has to be present")
  @Column(name = "week_number")
  private String weekNum;
  @NotNull(message = "interviewerId has to be present")
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
