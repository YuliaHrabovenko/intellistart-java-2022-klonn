package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * CandidateTimeSlot model.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "candidate_time_slots")
public class CandidateTimeSlot {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @NotNull(message = "date has to be present")
  @Column(name = "interview_date", nullable = false)
  private LocalDate date;
  @NotNull(message = "from has to be present")
  @Column(name = "start_time", nullable = false)
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @NotNull(message = "to has to be present")
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "end_time", nullable = false)
  private LocalTime to;
  @Column(name = "name")
  private String name;
  @Column(name = "email")
  private String email;
  @OneToMany
  @JoinColumn(name = "candidate_time_slot_id", referencedColumnName = "id")
  private List<Booking> bookingList = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param date exact date for interview
   * @param from start time
   * @param to   end time
   */
  public CandidateTimeSlot(LocalDate date, LocalTime from, LocalTime to) {
    this.date = date;
    this.from = from;
    this.to = to;
  }
}
