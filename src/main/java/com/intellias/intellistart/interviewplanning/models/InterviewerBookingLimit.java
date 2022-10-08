package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Class to describe a limit of bookings for interviewer.
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerBookingLimit {
  @Id
  protected Long interviewerId;
  protected Long weekNum;
  protected Integer limit;

}
