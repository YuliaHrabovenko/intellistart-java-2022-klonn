package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Candidate entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  List<User> findByRole(UserRole role);

  Optional<User> findUserByEmail(String email);
}
