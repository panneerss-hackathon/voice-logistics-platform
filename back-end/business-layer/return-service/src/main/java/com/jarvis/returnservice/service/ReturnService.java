package com.jarvis.returnservice.service;

import com.jarvis.returnservice.dto.ReturnRequest;
import com.jarvis.returnservice.entity.ReturnEvent;
import com.jarvis.returnservice.repository.ReturnEventRepository;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
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
public class ReturnService {

    private final ReturnEventRepository repository;
    private final JmsTemplate jmsTemplate;

    public void handleReturn(ReturnRequest request, String correlationId) {
        // Save to DB
        ReturnEvent event = ReturnEvent.builder()
                .orderId(request.getOrderId())
                .receiverAddress(request.getReceiverAddress())
                .reason(request.getReason())
                .eventType("RETURNED")
                .eventTime(LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        repository.save(event);

        // Prepare Azure Service Bus payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", event.getOrderId());
        payload.put("receiverAddress", event.getReceiverAddress());
        payload.put("reason", event.getReason());
        payload.put("eventType", event.getEventType());
        payload.put("eventTime", event.getEventTime().toString());
        payload.put("correlationId", correlationId);

        // Add required fields for tracking compatibility
        payload.put("trackingNumber", event.getOrderId()); // treating orderId as trackingNumber
        payload.put("status", "RETURNED");

        jmsTemplate.convertAndSend("shipment-events", payload);

        log.info("[{}] Return event published to Azure Service Bus for order: {}", correlationId, request.getOrderId());
    }
}