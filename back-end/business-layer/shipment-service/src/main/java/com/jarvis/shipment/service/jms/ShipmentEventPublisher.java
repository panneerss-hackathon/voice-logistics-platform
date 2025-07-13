package com.jarvis.shipment.service.jms;

import com.jarvis.shipment.dto.ShipmentEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventPublisher {

  private final JmsTemplate jmsTemplate;

  public void publish(ShipmentEventMessage message) {
    try {
      jmsTemplate.convertAndSend("shipment-events", message);
      log.info("📤 Published to Azure Bus: {}", message);
    } catch (Exception ex) {
      log.error("❌ Failed to publish shipment event to Azure Bus", ex);
    }
  }
}
