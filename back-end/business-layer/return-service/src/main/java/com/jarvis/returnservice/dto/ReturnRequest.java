package com.jarvis.returnservice.dto;

import lombok.Data;

@Data
public class ReturnRequest {
    private String orderId;
    private String receiverAddress;
    private String reason;
}
