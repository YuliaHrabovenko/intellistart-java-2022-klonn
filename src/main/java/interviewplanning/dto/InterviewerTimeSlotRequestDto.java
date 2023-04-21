package interviewplanning.dto;

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
  @JsonProperty("weekNum")
  @NotNull(message = "weekNum has to be present")
  private String weekNum;

  @JsonProperty("dayOfWeek")
  @NotNull(message = "dayOfWeek has to be present")
  private DayOfWeek day;

  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("from")
  @NotNull(message = "from has to be present")
  private LocalTime from;

  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("to")
  @NotNull(message = "to has to be present")
  private LocalTime to;
}
