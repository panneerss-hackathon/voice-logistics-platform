package com.jarvis.shipment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "shipment_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String trackingNumber;

  @Column(nullable = false)
  private String eventType;

  private String senderName;
  private String senderAddress;
  private String senderPhone;
  private String receiverName;
  private String receiverAddress;
  private String receiverPhone;
  private Double packageWeight;
  private String packageDescription;
  private Boolean pickupRequired;
  private String deliveryType;
  private String status;

  @Column(nullable = false)
  private LocalDateTime eventTime;

  private String correlationId;
}
