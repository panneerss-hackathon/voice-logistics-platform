package com.jarvis.tracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tracking_events")
public class TrackingEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String trackingNumber;
  private String eventType;
  private String status;
  private LocalDateTime eventTime;
  private String correlationId;
}
