package com.jarvis.reschedule.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reschedule_events")
public class RescheduleEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String trackingNumber;
  private LocalDate newDeliveryDate;
  private LocalTime newDeliveryTime;
  private String status;
  private String eventType;
  private LocalDateTime eventTime;
  private String correlationId;
}
