package com.intellias.intellistart.interviewplanning.models;


import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * User model.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id")
  private UUID id;
  @Column(name = "email")
  private String email;
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "interviewer_id", referencedColumnName = "id")
  private Set<InterviewerTimeSlot> interviewerTimeSlots;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "candidate_id", referencedColumnName = "id")
  private Set<CandidateTimeSlot> candidateTimeSlots;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "interviewer_id", referencedColumnName = "id")
  private Set<InterviewerBookingLimit> interviewerBookingLimits;

  /**
   * Constructor.
   *
   * @param email email of the user
   * @param role  role of the user
   */
  public User(String email, UserRole role) {
    this.email = email;
    this.role = role;
  }

  /**
   * Constructor.
   *
   * @param id    user id
   * @param email user email
   * @param role  user role
   */

  public User(UUID id, String email, UserRole role) {
    this.id = id;
    this.email = email;
    this.role = role;
  }
}
