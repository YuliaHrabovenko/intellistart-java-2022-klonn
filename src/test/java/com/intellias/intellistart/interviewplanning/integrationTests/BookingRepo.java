package com.intellias.intellistart.interviewplanning.integrationTests;

import com.intellias.intellistart.interviewplanning.models.Booking;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, UUID> {

}
