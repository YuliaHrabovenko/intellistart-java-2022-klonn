package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for Candidate time slot request.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateTimeSlotRequestDto {
  @JsonProperty("date")
  private LocalDate date;

  @JsonProperty("from")
  private LocalTime from;

  @JsonProperty("to")
  private LocalTime to;
}
