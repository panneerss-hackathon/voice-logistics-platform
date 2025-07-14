package com.jarvis.returnservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String receiverAddress;
    private String reason;
    private String eventType;
    private String correlationId;
    private LocalDateTime eventTime;
}
