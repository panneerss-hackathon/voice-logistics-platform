package com.jarvis.reschedule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarvis.reschedule.dto.RescheduleRequest;
import com.jarvis.reschedule.entity.RescheduleEvent;
import com.jarvis.reschedule.repository.RescheduleEventRepository;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RescheduleService {

  private final JmsTemplate jmsTemplate;
  private final RescheduleEventRepository repository;
  private final ObjectMapper objectMapper;

  public void sendRescheduleEvent(RescheduleRequest req, String correlationId) {
    RescheduleEvent entity = RescheduleEvent.builder()
            .trackingNumber(req.getTrackingNumber())
            .newDeliveryDate(req.getNewDeliveryDate())
            .newDeliveryTime(req.getNewDeliveryTime())
            .eventTime(LocalDateTime.now())
            .eventType("RESCHEDULED")
            .status("RESCHEDULED")
            .correlationId(correlationId)
            .build();

    repository.save(entity);

    Map<String, Object> payload = new HashMap<>();
    payload.put("trackingNumber", entity.getTrackingNumber());
    payload.put("eventType", entity.getEventType());
    payload.put("status", entity.getStatus());
    payload.put("newDeliveryDate", entity.getNewDeliveryDate().toString());
    payload.put("newDeliveryTime", entity.getNewDeliveryTime().toString());
    payload.put("eventTime", entity.getEventTime().toString());
    payload.put("correlationId", entity.getCorrelationId());

    try {
      jmsTemplate.send("shipment-events", new MessageCreator() {
        @Override
        public Message createMessage(Session session) {
          try {
            String json = objectMapper.writeValueAsString(payload);
            TextMessage message = session.createTextMessage(json);
            message.setStringProperty("correlationId", correlationId);
            return message;
          } catch (JsonProcessingException | jakarta.jms.JMSException e) {
            log.error("Failed to serialize or create JMS message", e);
            throw new RuntimeException("JMS Message creation failed", e);
          }
        }
      });

      log.info("[{}] RESCHEDULED event sent to 'shipment-events' for tracking number: {}", correlationId, entity.getTrackingNumber());

    } catch (Exception e) {
      log.error("[{}] Failed to send RESCHEDULED event", correlationId, e);
      throw new RuntimeException("JMS send operation failed", e);
    }
  }
}