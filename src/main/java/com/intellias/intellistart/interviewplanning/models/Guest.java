package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract model Guest with basic fields.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class Guest {
  @Id
  protected Long id;
  protected String firstName;
  protected String lastName;
  protected String email;

  /**
   * Constructor.
   *
   * @param id        id of guest in the db
   * @param firstName first name of the guest
   * @param lastName  last name of the guest
   * @param email     email of the guest
   */
  public Guest(Long id, String firstName, String lastName, String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }
}
