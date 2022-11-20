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
  @NotNull(message = "weekBookingLimit has to be present")
  @Min(value = 1, message = "Week booking limit must be greater or equal to {value}")
  @JsonProperty("weekBookingLimit")
  private Integer weekBookingLimit;

  @NotNull(message = "weekNum has to be present")
  @JsonProperty("weekNum")
  private String weekNum;
}
