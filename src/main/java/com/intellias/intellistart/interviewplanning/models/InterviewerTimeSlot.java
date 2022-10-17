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
  @Column(name = "day_of_week")
  private DayOfWeek dayOfWeek;
  @Column(name = "start_time")
  private LocalTime from;
  @Column(name = "end_time")
  private LocalTime to;
  @Column(name = "interviewer_id")
  private UUID interviewerId;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "interviewer_time_slot_id", referencedColumnName = "id")
  private List<Booking> bookingList = new ArrayList<>();
  @JsonUnwrapped
  @ManyToOne
  @JoinColumn(name = "week_id")
  private Week week;

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
