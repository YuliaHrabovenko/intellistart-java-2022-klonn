package interviewplanning.integrationTests;

import interviewplanning.models.Booking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, UUID> {

}
