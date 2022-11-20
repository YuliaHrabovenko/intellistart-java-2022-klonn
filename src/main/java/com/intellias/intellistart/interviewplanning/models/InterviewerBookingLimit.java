package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  @JsonProperty("weekBookingLimit")
  @Column(name = "week_booking_limit", nullable = false)
  private Integer weekBookingLimit;
  @JsonProperty("currentBookingCount")
  @Column(name = "current_booking_count")
  private Integer currentBookingCount = 0;
  @JsonProperty("weekNum")
  @Column(name = "week_number", nullable = false)
  private String weekNum;
  @JsonProperty("interviewerId")
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
