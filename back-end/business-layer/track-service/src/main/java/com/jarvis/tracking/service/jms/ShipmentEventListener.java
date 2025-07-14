package com.jarvis.tracking.service.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarvis.tracking.entity.TrackingEvent;
import com.jarvis.tracking.repository.TrackingEventRepository;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventListener {

  private final ObjectMapper objectMapper;
  private final TrackingEventRepository repository;

  @JmsListener(destination = "shipment-events")
  public void receiveEvent(Message message) {
    try {
      if (message instanceof TextMessage textMessage) {
        Map<String, Object> payload = objectMapper.readValue(textMessage.getText(), Map.class);
        String trackingNumber = (String) payload.get("trackingNumber");
        String eventType = (String) payload.get("eventType");
        String status = (String) payload.get("status");
        String correlationId = (String) payload.getOrDefault("correlationId", "N/A");

        TrackingEvent event =
            TrackingEvent.builder()
                .trackingNumber(trackingNumber)
                .eventType(eventType)
                .status(status)
                .eventTime(LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        repository.save(event);

        log.info(
            "[{}] Saved {} event for tracking number {}", correlationId, eventType, trackingNumber);
      }
    } catch (Exception e) {
      log.error("Failed to process shipment event", e);
    }
  }
}
