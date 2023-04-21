package interviewplanning.integrationTests;

import interviewplanning.models.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTestRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
}