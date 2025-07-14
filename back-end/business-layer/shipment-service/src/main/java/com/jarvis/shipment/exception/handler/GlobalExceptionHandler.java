package com.jarvis.shipment.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception ex) {
    String correlationId = MDC.get("correlationId");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage(), correlationId));
  }

  public record ErrorResponse(String message, String correlationId) {}
}
