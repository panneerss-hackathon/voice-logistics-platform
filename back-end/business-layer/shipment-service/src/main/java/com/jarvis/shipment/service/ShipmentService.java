package com.jarvis.shipment.service;

import com.jarvis.shipment.config.EntityConfigProperties;
import com.jarvis.shipment.dto.ShipmentEventMessage;
import com.jarvis.shipment.dto.ShipmentHistoryResponse;
import com.jarvis.shipment.dto.ShipmentRequest;
import com.jarvis.shipment.dto.ShipmentResponse;
import com.jarvis.shipment.entity.ShipmentEvent;
import com.jarvis.shipment.repository.ShipmentEventRepository;
import com.jarvis.shipment.service.jms.ShipmentEventPublisher;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentService {

  private final ShipmentEventRepository eventRepository;
  private final EntityConfigProperties config;
  private final ShipmentEventPublisher publisher;

  @Transactional
  public ShipmentResponse createShipment(ShipmentRequest req, String correlationId) {
    var caseCfg = config.getCases().get("CreateShipment");
    if (caseCfg == null) throw new IllegalArgumentException("Config missing for CreateShipment");

    for (String f : caseCfg.getRequiredFields()) {
      Object v = getFieldValue(req, f);
      if (v == null) throw new IllegalArgumentException("Missing required field: " + f);
    }

    String trackingNumber = generateTrackingNumberWithTimestamp();

    ShipmentEvent event =
        ShipmentEvent.builder()
            .trackingNumber(trackingNumber)
            .eventType("CREATED")
            .senderName(req.getSenderName())
            .senderAddress(req.getSenderAddress())
            .senderPhone(req.getSenderPhone())
            .receiverName(req.getReceiverName())
            .receiverAddress(req.getReceiverAddress())
            .receiverPhone(req.getReceiverPhone())
            .packageWeight(req.getPackageWeight())
            .packageDescription(req.getPackageDescription())
            .pickupRequired(req.getPickupRequired())
            .deliveryType(req.getDeliveryType())
            .status("CREATED")
            .eventTime(LocalDateTime.now())
            .correlationId(correlationId)
            .build();

    eventRepository.save(event);

    // Publish to Azure Service Bus
    publisher.publish(
        ShipmentEventMessage.builder()
            .trackingNumber(trackingNumber)
            .status("CREATED")
            .eventType("CREATED")
            .eventTime(event.getEventTime())
            .build());

    log.info("[{}] Shipment created: {}", correlationId, trackingNumber);

    return ShipmentResponse.builder()
        .trackingNumber(trackingNumber)
        .status("CREATED")
        .message("Shipment created")
        .correlationId(correlationId)
        .build();
  }

  @Transactional
  public ShipmentResponse updateShipment(
      String trackingNumber, ShipmentRequest req, String correlationId) {
    ShipmentEvent lastEvent =
        eventRepository.findTopByTrackingNumberOrderByEventTimeDesc(trackingNumber);
    if (lastEvent == null)
      throw new IllegalArgumentException("Shipment not found: " + trackingNumber);

    ShipmentEvent event =
        ShipmentEvent.builder()
            .trackingNumber(trackingNumber)
            .eventType("UPDATED")
            .senderName(
                req.getSenderName() != null ? req.getSenderName() : lastEvent.getSenderName())
            .senderAddress(
                req.getSenderAddress() != null
                    ? req.getSenderAddress()
                    : lastEvent.getSenderAddress())
            .senderPhone(
                req.getSenderPhone() != null ? req.getSenderPhone() : lastEvent.getSenderPhone())
            .receiverName(
                req.getReceiverName() != null ? req.getReceiverName() : lastEvent.getReceiverName())
            .receiverAddress(
                req.getReceiverAddress() != null
                    ? req.getReceiverAddress()
                    : lastEvent.getReceiverAddress())
            .receiverPhone(
                req.getReceiverPhone() != null
                    ? req.getReceiverPhone()
                    : lastEvent.getReceiverPhone())
            .packageWeight(
                req.getPackageWeight() != null
                    ? req.getPackageWeight()
                    : lastEvent.getPackageWeight())
            .packageDescription(
                req.getPackageDescription() != null
                    ? req.getPackageDescription()
                    : lastEvent.getPackageDescription())
            .pickupRequired(
                req.getPickupRequired() != null
                    ? req.getPickupRequired()
                    : lastEvent.getPickupRequired())
            .deliveryType(
                req.getDeliveryType() != null ? req.getDeliveryType() : lastEvent.getDeliveryType())
            .status("UPDATED")
            .eventTime(LocalDateTime.now())
            .correlationId(correlationId)
            .build();

    eventRepository.save(event);

    // Publish to Azure Service Bus
    publisher.publish(
        ShipmentEventMessage.builder()
            .trackingNumber(trackingNumber)
            .status("UPDATED")
            .eventType("UPDATED")
            .eventTime(event.getEventTime())
            .build());

    log.info("[{}] Shipment updated: {}", correlationId, trackingNumber);

    return ShipmentResponse.builder()
        .trackingNumber(trackingNumber)
        .status("UPDATED")
        .message("Shipment updated")
        .correlationId(correlationId)
        .build();
  }

  public ShipmentHistoryResponse getHistory(String trackingNumber, String correlationId) {
    List<ShipmentEvent> events =
        eventRepository.findByTrackingNumberOrderByEventTimeAsc(trackingNumber);
    return ShipmentHistoryResponse.builder()
        .trackingNumber(trackingNumber)
        .correlationId(correlationId)
        .history(
            events.stream()
                .map(
                    e ->
                        ShipmentHistoryResponse.HistoryEvent.builder()
                            .eventType(e.getEventType())
                            .status(e.getStatus())
                            .eventTime(e.getEventTime().toString())
                            .snapshot(
                                ShipmentHistoryResponse.ShipmentSnapshot.builder()
                                    .senderName(e.getSenderName())
                                    .senderAddress(e.getSenderAddress())
                                    .senderPhone(e.getSenderPhone())
                                    .receiverName(e.getReceiverName())
                                    .receiverAddress(e.getReceiverAddress())
                                    .receiverPhone(e.getReceiverPhone())
                                    .packageWeight(e.getPackageWeight())
                                    .packageDescription(e.getPackageDescription())
                                    .pickupRequired(e.getPickupRequired())
                                    .deliveryType(e.getDeliveryType())
                                    .status(e.getStatus())
                                    .build())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  // Tracking number with timestamp (e.g., TRK-ABC12345-202507141415)
  private String generateTrackingNumberWithTimestamp() {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    Random rnd = new Random();
    StringBuilder sb = new StringBuilder("TRK-");
    for (int i = 0; i < 8; i++) {
      sb.append(chars.charAt(rnd.nextInt(chars.length())));
    }
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    return sb + "-" + timestamp;
  }

  private Object getFieldValue(ShipmentRequest req, String field) {
    try {
      var fld = ShipmentRequest.class.getDeclaredField(toCamelCase(field));
      fld.setAccessible(true);
      return fld.get(req);
    } catch (Exception e) {
      return null;
    }
  }

  private String toCamelCase(String s) {
    String[] parts = s.split("_");
    StringBuilder b = new StringBuilder(parts[0]);
    for (int i = 1; i < parts.length; i++)
      b.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
    return b.toString();
  }
}
