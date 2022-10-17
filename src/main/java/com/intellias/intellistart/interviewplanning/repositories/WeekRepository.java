package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.Week;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Week entity.
 */
@Repository
public interface WeekRepository extends JpaRepository<Week, UUID> {
  @Query(value = "select * from weeks w where w.week_number = ?1",
      nativeQuery = true)
  Week getWeekByWeekNumber(String weekNum);
}
