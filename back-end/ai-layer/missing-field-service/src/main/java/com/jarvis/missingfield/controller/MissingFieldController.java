package com.jarvis.missingfield.controller;

import com.jarvis.missingfield.dto.MissingFieldRequest;
import com.jarvis.missingfield.dto.MissingFieldResponse;
import com.jarvis.missingfield.service.MissingFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/missing-fields")
@RequiredArgsConstructor
@Slf4j
public class MissingFieldController {

  private final MissingFieldService service;

  @PostMapping
  public ResponseEntity<MissingFieldResponse> findMissingFields(
      @Valid @RequestBody MissingFieldRequest request,
      @RequestHeader(value = "x-correlation-id", required = false) String correlationId) {
    // Set correlation ID for downstream logging and tracing
    if (correlationId != null && !correlationId.isBlank()) {
      MDC.put("correlationId", correlationId);
    }
    log.info("Received missing-field request: {}", request);

    MissingFieldResponse response = service.processRequest(request);
    log.info("Missing fields found: {}", response);

    MDC.clear();
    return ResponseEntity.ok(response);
  }
}
