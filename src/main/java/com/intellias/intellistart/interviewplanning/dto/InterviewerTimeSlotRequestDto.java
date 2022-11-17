package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;
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
  @JsonProperty("weekNum")
  private String weekNum;

  @JsonProperty("dayOfWeek")
  private DayOfWeek day;

  @JsonProperty("from")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;

  @JsonProperty("to")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
}
