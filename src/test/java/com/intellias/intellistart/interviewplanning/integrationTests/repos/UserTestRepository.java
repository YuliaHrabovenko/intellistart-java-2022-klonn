package com.intellias.intellistart.interviewplanning.integrationTests.repos;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTestRepository extends JpaRepository<User, UUID>{

    Optional<User> findUserByEmail(String email);
  }