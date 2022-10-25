package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
  @NotNull(message = "dayOfWeek has to be present")
  @Column(name = "day_of_week")
  private DayOfWeek dayOfWeek;
  @NotNull(message = "from has to be present")
  @Column(name = "start_time")
  private LocalTime from;
  @NotNull(message = "to has to be present")
  @Column(name = "end_time")
  private LocalTime to;
  @NotNull(message = "weekNumber has to be present")
  @Column(name = "week_number")
  private String weekNum;
  @NotNull(message = "interviewerId has to be present")
  @Column(name = "interviewer_id")
  private UUID interviewerId;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
