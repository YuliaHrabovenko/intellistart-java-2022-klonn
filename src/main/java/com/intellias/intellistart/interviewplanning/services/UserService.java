package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.dto.UserDto;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public UserService(UserRepository userRepository, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Get interviewer or coordinator by email.
   *
   * @param email email
   * @return interviewer or coordinator object
   */
  public UserDto getByEmail(String email) {
    User user = userRepository.findByEmail(email).orElseGet(() -> User.builder()
            .email(email)
            .role(UserRole.CANDIDATE)
            .build()
        );
    return mapToUserDto(user);
  }

  public UserDto mapToUserDto(User user) {
    return modelMapper.map(user, UserDto.class);
  }
}
