package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.Booking;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
  @Query(value = "select * from bookings b where b.candidate_time_slot_id = ?1", nativeQuery = true)
  List<Booking> getBookingsByCandidateSlotId(UUID candidateSlotId);
}
