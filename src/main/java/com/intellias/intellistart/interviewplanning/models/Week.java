package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * Week model.
 */
@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "weeks")
public class Week {
  @JsonIgnore
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "week_number")
  private String weekNumber;
  @JsonIgnore
  @OneToMany(mappedBy = "week")
  private List<InterviewerBookingLimit> interviewerBookingLimits = new ArrayList<>();
  @JsonIgnore
  @OneToMany(mappedBy = "week")
  private List<InterviewerTimeSlot> interviewerTimeSlots = new ArrayList<>();
}
