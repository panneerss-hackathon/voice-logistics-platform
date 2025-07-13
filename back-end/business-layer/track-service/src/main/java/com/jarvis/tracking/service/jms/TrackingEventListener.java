package com.jarvis.tracking.service.jms;

import com.jarvis.tracking.entity.TrackingEvent;
import com.jarvis.tracking.repository.TrackingEventRepository;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrackingEventListener {

    private final TrackingEventRepository repository;

    @JmsListener(destination = "shipment-events", containerFactory = "jmsListenerContainerFactory")
    public void receiveEvent(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) objectMessage.getObject();

            String trackingNumber = (String) payload.get("trackingNumber");
            String status = (String) payload.get("status");
            String eventType = (String) payload.get("eventType");
            String eventTimeStr = (String) payload.get("eventTime");

            TrackingEvent event = TrackingEvent.builder()
                    .trackingNumber(trackingNumber)
                    .status(status)
                    .eventType(eventType)
                    .eventTime(LocalDateTime.parse(eventTimeStr))
                    .build();

            repository.save(event);
            log.info("✅ Saved tracking event for: {}", trackingNumber);
        } catch (Exception e) {
            log.error("❌ Failed to process incoming shipment event", e);
        }
    }
}