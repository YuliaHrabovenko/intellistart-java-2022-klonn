package com.intellias.intellistart.interviewplanning.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class UserDto {
  private String email;
  private UserRole role;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private UUID id;
}
