package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class for setting interviewer booking limit form.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerBookingLimitDto {
  @NotNull(message = "week_booking_limit has to be present")
  @Min(value = 1, message = "Week booking limit must be greater or equal to {value}")
  @JsonProperty("week_booking_limit")
  private Integer weekBookingLimit;

  @NotNull(message = "week_num has to be present")
  @JsonProperty("week_num")
  private String weekNum;
}
