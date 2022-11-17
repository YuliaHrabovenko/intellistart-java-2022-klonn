package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
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
  @NotNull(message = "date has to be present")
  private LocalDate date;

  @NotNull(message = "from has to be present")
  private LocalTime from;

  @NotNull(message = "to has to be present")
  private LocalTime to;
}
