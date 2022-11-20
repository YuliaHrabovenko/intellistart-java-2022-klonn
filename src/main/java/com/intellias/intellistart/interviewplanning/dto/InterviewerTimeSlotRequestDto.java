package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for Interviewer time slot request.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerTimeSlotRequestDto {
  @NotNull(message = "weekNum has to be present")
  @JsonProperty("weekNum")
  private String weekNum;

  @NotNull(message = "dayOfWeek has to be present")
  @JsonProperty("dayOfWeek")
  private DayOfWeek day;

  @NotNull(message = "from has to be present")
  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("from")
  private LocalTime from;

  @NotNull(message = "to has to be present")
  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("to")
  private LocalTime to;
}
