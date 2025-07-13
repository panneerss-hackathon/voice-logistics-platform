package com.jarvis.shipment.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentHistoryResponse {
  private String trackingNumber;
  private List<HistoryEvent> history;
  private String correlationId;

  @Data
  @Builder
  public static class HistoryEvent {
    private String eventType;
    private String status;
    private String eventTime;
    private ShipmentSnapshot snapshot;
  }

  @Data
  @Builder
  public static class ShipmentSnapshot {
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
  }
}
