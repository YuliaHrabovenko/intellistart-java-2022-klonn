package interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * InterviewerTimeSlot model.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "interviewer_time_slots")
public class InterviewerTimeSlot {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @JsonProperty("dayOfWeek")
  @Column(name = "day_of_week", nullable = false)
  private DayOfWeek dayOfWeek;
  @JsonProperty("from")
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "start_time", nullable = false)
  private LocalTime from;
  @JsonProperty("to")
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "end_time", nullable = false)
  private LocalTime to;
  @JsonProperty("weekNum")
  @Column(name = "week_number", nullable = false)
  private String weekNum;
  @JsonProperty("interviewerId")
  @Column(name = "interviewer_id")
  private UUID interviewerId;
  @JsonProperty("bookings")
  @OneToMany
  @JoinColumn(name = "interviewer_time_slot_id", referencedColumnName = "id")
  private List<Booking> bookingList = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param dayOfWeek     day of week
   * @param from          start time
   * @param to            end time
   * @param interviewerId interviewer`s id
   */
  public InterviewerTimeSlot(DayOfWeek dayOfWeek, LocalTime from, LocalTime to,
                             UUID interviewerId) {
    this.dayOfWeek = dayOfWeek;
    this.from = from;
    this.to = to;
    this.interviewerId = interviewerId;
  }
}
