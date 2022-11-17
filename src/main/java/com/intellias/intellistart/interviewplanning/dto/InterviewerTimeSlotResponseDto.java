package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for interviewer time slot response.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerTimeSlotResponseDto extends InterviewerTimeSlotRequestDto {
  @JsonProperty("id")
  private UUID id;

  @JsonProperty("weekNum")
  private String weekNum;

  @JsonProperty("dayOfWeek")
  private DayOfWeek day;

  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("from")
  private LocalTime from;

  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("to")
  private LocalTime to;
}
