package interviewplanning.repositories;

import interviewplanning.models.CandidateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, UUID> {
  List<CandidateTimeSlot> findByDateBetween(LocalDate from, LocalDate to);

  List<CandidateTimeSlot> findByEmail(String email);

  List<CandidateTimeSlot> findByEmailAndDate(String email, LocalDate date);
}
