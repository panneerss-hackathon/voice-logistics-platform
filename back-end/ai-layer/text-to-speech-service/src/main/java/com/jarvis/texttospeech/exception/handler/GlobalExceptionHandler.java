package com.jarvis.texttospeech.exception.handler;

import com.jarvis.texttospeech.exception.TextToSpeechException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(TextToSpeechException.class)
  public ResponseEntity<Map<String, Object>> handleTtsException(
      TextToSpeechException ex, HttpServletRequest request) {
    String correlationId = request.getHeader("X-Correlation-ID");
    log.error("❌ TTS Exception | id={} | msg={}", correlationId, ex.getMessage(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), correlationId);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String correlationId = request.getHeader("X-Correlation-ID");

    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();

    log.warn("⚠️ Validation failed | id={} | errors={}", correlationId, errors);
    return ResponseEntity.badRequest()
        .body(
            Map.of(
                "status", "VALIDATION_FAILED",
                "errors", errors,
                "correlationId", correlationId));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(
      Exception ex, HttpServletRequest request) {
    String correlationId = request.getHeader("X-Correlation-ID");
    log.error("🔥 Unexpected error | id={} | msg={}", correlationId, ex.getMessage(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", correlationId);
  }

  private ResponseEntity<Map<String, Object>> buildResponse(
      HttpStatus status, String message, String correlationId) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "status", status.getReasonPhrase(),
                "message", message,
                "correlationId", correlationId));
  }
}
