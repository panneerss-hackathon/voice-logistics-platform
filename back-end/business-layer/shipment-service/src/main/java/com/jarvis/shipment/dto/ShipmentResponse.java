package com.jarvis.shipment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentResponse {
  private String trackingNumber;
  private String status;
  private String message;
  private String correlationId;
}
