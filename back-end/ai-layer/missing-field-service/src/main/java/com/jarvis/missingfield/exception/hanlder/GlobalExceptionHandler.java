package com.jarvis.missingfield.exception.hanlder;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAll(Exception ex) {
    log.error("Exception: ", ex);
    return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
  }
}
