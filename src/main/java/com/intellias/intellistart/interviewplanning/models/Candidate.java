package com.intellias.intellistart.interviewplanning.models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Candidate model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Candidate extends Guest {

  @OneToMany
  private List<CandidateTimeSlot> slots;

  public Candidate(long id, String firstName, String lastName, String email) {
    super(id, firstName, lastName, email);
  }

}
