package com.intellias.intellistart.interviewplanning.models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Interviewer model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Interviewer extends Guest {
  private Integer maxBookingCount;
  @OneToMany
  private List<InterviewerTimeSlot> slots;

  /**
   * Constructor.
   *
   * @param id              the id of an interviewer in db
   * @param firstName       the first name of an interviewer
   * @param lastName        the last name of an interviewer
   * @param email           the email of an interviewer
   * @param maxBookingCount maximum booking number per week
   * @param slots           interviewer`s slots
   */
  public Interviewer(long id,
                     String firstName,
                     String lastName,
                     String email,
                     int maxBookingCount,
                     List<InterviewerTimeSlot> slots) {
    super(id, firstName, lastName, email);
    this.maxBookingCount = maxBookingCount;
    this.slots = slots;
  }
}
