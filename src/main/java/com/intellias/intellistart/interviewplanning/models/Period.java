package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Period model.
 */
@Data
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "periods")
public class Period {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "uuid")
  private UUID uuid = UUID.randomUUID();
  @Column(name = "startTime")
  private LocalDateTime from;
  @Column(name = "endTime")
  private LocalDateTime to;

  public Period(LocalDateTime from, LocalDateTime to) {
    this.from = from;
    this.to = to;
  }
}
