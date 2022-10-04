package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Coordinator model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Coordinator extends Guest {

  public Coordinator(long id, String firstName, String lastName, String email) {
    super(id, firstName, lastName, email);
  }

}
