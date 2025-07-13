package com.jarvis.reschedule.service;

import com.jarvis.reschedule.dto.RescheduleRequest;
import com.jarvis.reschedule.entity.RescheduleEvent;
import com.jarvis.reschedule.repository.RescheduleEventRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RescheduleService {

  private final JmsTemplate jmsTemplate;
  private final RescheduleEventRepository repository;

  public void sendRescheduleEvent(RescheduleRequest req, String correlationId) {
    RescheduleEvent entity =
        RescheduleEvent.builder()
            .trackingNumber(req.getTrackingNumber())
            .newDeliveryDate(req.getNewDeliveryDate())
            .newDeliveryTime(req.getNewDeliveryTime())
            .eventTime(LocalDateTime.now())
            .eventType("RESCHEDULED")
            .status("RESCHEDULED")
            .correlationId(correlationId)
            .build();

    repository.save(entity);

    Map<String, Object> event = new HashMap<>();
    event.put("trackingNumber", entity.getTrackingNumber());
    event.put("eventType", entity.getEventType());
    event.put("status", entity.getStatus());
    event.put("newDeliveryDate", entity.getNewDeliveryDate().toString());
    event.put("newDeliveryTime", entity.getNewDeliveryTime().toString());
    event.put("eventTime", entity.getEventTime().toString());
    event.put("correlationId", entity.getCorrelationId());

    jmsTemplate.convertAndSend("shipment-events", event);

    log.info(
        "[{}] RESCHEDULED event sent for tracking number: {}",
        correlationId,
        entity.getTrackingNumber());
  }
}
