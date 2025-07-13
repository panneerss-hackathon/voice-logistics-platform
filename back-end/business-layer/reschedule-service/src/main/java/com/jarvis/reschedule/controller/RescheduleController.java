package com.jarvis.reschedule.controller;

import com.jarvis.reschedule.dto.RescheduleRequest;
import com.jarvis.reschedule.service.RescheduleService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reschedule")
@RequiredArgsConstructor
@Slf4j
public class RescheduleController {

  private final RescheduleService service;

  @PostMapping
  public ResponseEntity<String> reschedule(
      @Valid @RequestBody RescheduleRequest request,
      @RequestHeader(value = "x-correlation-id", required = false) String correlationId) {
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = UUID.randomUUID().toString();
    }
    service.sendRescheduleEvent(request, correlationId);
    return ResponseEntity.ok("Reschedule event published.");
  }
}
