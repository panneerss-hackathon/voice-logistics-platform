package com.jarvis.shipment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentEventMessage {
  private String trackingNumber;
  private String status;
  private String eventType;
  private LocalDateTime eventTime;
}
