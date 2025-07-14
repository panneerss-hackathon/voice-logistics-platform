package com.jarvis.shipment.dto;

import lombok.Data;

@Data
public class ShipmentRequest {
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
}
