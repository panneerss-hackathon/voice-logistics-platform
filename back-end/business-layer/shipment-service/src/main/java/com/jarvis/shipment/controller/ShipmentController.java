package com.jarvis.shipment.controller;

import com.jarvis.shipment.dto.ShipmentHistoryResponse;
import com.jarvis.shipment.dto.ShipmentRequest;
import com.jarvis.shipment.dto.ShipmentResponse;
import com.jarvis.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
  private final ShipmentService shipmentService;

  @Operation(summary = "Create a new shipment (immutable event)")
  @PostMapping
  public ResponseEntity<ShipmentResponse> create(
      @RequestBody ShipmentRequest req,
      @RequestHeader(value = "x-correlation-id", required = false) String correlationIdHeader) {
    String correlationId =
        correlationIdHeader != null ? correlationIdHeader : java.util.UUID.randomUUID().toString();
    MDC.put("correlationId", correlationId);
    try {
      return ResponseEntity.ok(shipmentService.createShipment(req, correlationId));
    } finally {
      MDC.clear();
    }
  }

  @Operation(summary = "Update a shipment (partial, immutable event)")
  @PatchMapping("/{trackingNumber}")
  public ResponseEntity<ShipmentResponse> update(
      @PathVariable String trackingNumber,
      @RequestBody ShipmentRequest req,
      @RequestHeader(value = "x-correlation-id", required = false) String correlationIdHeader) {
    String correlationId =
        correlationIdHeader != null ? correlationIdHeader : java.util.UUID.randomUUID().toString();
    MDC.put("correlationId", correlationId);
    try {
      return ResponseEntity.ok(shipmentService.updateShipment(trackingNumber, req, correlationId));
    } finally {
      MDC.clear();
    }
  }

  @Operation(summary = "Get full shipment change history (all events, immutable)")
  @GetMapping("/{trackingNumber}/history")
  public ResponseEntity<ShipmentHistoryResponse> history(
      @PathVariable String trackingNumber,
      @RequestHeader(value = "x-correlation-id", required = false) String correlationIdHeader) {
    String correlationId =
        correlationIdHeader != null ? correlationIdHeader : java.util.UUID.randomUUID().toString();
    MDC.put("correlationId", correlationId);
    try {
      return ResponseEntity.ok(shipmentService.getHistory(trackingNumber, correlationId));
    } finally {
      MDC.clear();
    }
  }
}
