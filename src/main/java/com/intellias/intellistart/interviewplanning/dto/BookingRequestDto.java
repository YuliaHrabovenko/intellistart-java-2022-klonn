package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @NotNull(message = "from has to be present")
  private LocalTime from;

  @NotNull(message = "to has to be present")
  private LocalTime to;

  @NotNull(message = "interviewer_time_slot_id has to be present")
  @JsonProperty("interviewer_time_slot_id")
  private UUID interviewerTimeSlotId;

  @NotNull(message = "candidate_time_slot_id has to be present")
  @JsonProperty("candidate_time_slot_id")
  private UUID candidateTimeSlotId;

  @NotBlank
  @NotNull(message = "subject has to be present")
  @Size(max = 255, message = "Subject's length can't be higher than 255 characters")
  private String subject;

  @NotBlank
  @NotNull(message = "description has to be present")
  @Size(max = 4000, message = "Description's length can't be higher than 4000 characters")
  private String description;

}
