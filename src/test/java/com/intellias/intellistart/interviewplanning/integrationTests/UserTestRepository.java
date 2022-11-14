package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTestRepository extends JpaRepository<User, UUID> {

  Optional<User> findUserByEmail(String email);
}