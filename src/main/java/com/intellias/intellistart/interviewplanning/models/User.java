package com.intellias.intellistart.interviewplanning.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User model.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  protected Long id;
  protected String email;
  protected UserRole role;

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
}
