package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Booking request.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
  @JsonProperty("from")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;

  @JsonProperty("to")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;

  @JsonProperty("interviewerTimeSlotId")
  private UUID interviewerTimeSlotId;

  @JsonProperty("candidateTimeSlotId")
  private UUID candidateTimeSlotId;

  @JsonProperty("subject")
  private String subject;

  @JsonProperty("description")
  private String description;

}
