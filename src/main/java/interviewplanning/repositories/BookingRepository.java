package interviewplanning.repositories;

import interviewplanning.models.Booking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

  List<Booking> findByCandidateTimeSlotId(UUID candidateTimeSlotId);

  List<Booking> findByInterviewerTimeSlotId(UUID interviewerTimeSlotId);
}
