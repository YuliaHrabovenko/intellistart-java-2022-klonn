package com.intellias.intellistart.interviewplanning.dto;


import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class for user requests by INTERVIEWER or COORDINATOR role.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private String email;
  private UserRole role;
  private UUID id;
}
