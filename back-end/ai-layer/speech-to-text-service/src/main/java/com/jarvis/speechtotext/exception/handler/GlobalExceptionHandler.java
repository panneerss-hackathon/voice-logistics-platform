package com.jarvis.speechtotext.exception.handler;

import com.jarvis.speechtotext.exception.TranscriptionException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, String>> handleMaxSizeException(
      MaxUploadSizeExceededException ex) {
    log.warn("Max upload size exceeded: {}", ex.getMessage());
    var error = new HashMap<String, String>();
    error.put("error", "File too large. Maximum allowed size is 10MB.");
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TranscriptionException.class)
  public ResponseEntity<Map<String, String>> handleTranscriptionException(
      TranscriptionException ex) {
    log.error("TranscriptionException occurred", ex);
    var error = new HashMap<String, String>();
    error.put("error", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    log.error("Unhandled exception occurred", ex);
    var error = new HashMap<String, String>();
    error.put("error", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
