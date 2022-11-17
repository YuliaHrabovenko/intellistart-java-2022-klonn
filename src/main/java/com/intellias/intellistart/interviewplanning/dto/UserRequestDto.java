package com.intellias.intellistart.interviewplanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto for user request.
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
  private String email;
}
