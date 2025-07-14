package com.jarvis.speechtotext.controller;

import com.jarvis.speechtotext.exception.TranscriptionException;
import com.jarvis.speechtotext.service.SpeechToTextService;
import com.jarvis.speechtotext.service.response.TranscriptionResponse;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/speech")
public class SpeechToTextController {

  private static final List<String> SUPPORTED_TYPES =
      List.of("audio/wav", "audio/x-wav", "audio/m4a", "audio/x-m4a", "audio/mp3", "audio/mpeg");

  @Autowired private SpeechToTextService sttService;

  @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<TranscriptionResponse> transcribeAudio(
      @RequestParam("audio") MultipartFile file,
      @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

    if (file == null || file.isEmpty() || file.getSize() == 0) {
      log.warn("Correlation-ID [{}]: Empty or missing audio file", correlationId);
      throw new TranscriptionException("Audio file is empty or missing.");
    }

    if (!SUPPORTED_TYPES.contains(file.getContentType())) {
      log.warn(
          "Correlation-ID [{}]: Unsupported content type: {}",
          correlationId,
          file.getContentType());
      throw new TranscriptionException(
          "Unsupported file format. Please upload .wav, .mp3, or .m4a.");
    }

    try {
      var originalFilename =
          StringUtils.hasText(file.getOriginalFilename())
              ? file.getOriginalFilename()
              : "uploaded-audio";
      var cleanedFilename = StringUtils.cleanPath(originalFilename);
      var filePath = Paths.get(System.getProperty("java.io.tmpdir"), cleanedFilename).toFile();

      try (var fos = new FileOutputStream(filePath)) {
        fos.write(file.getBytes());
      }

      log.info(
          "Correlation-ID [{}]: Saved file at '{}'", correlationId, filePath.getAbsolutePath());
      var transcript = sttService.transcribeAudioFile(filePath, correlationId);
      log.info("Correlation-ID [{}]: Transcription successful", correlationId);

      return ResponseEntity.ok(transcript);
    } catch (Exception e) {
      log.error("Correlation-ID [{}]: Transcription failed", correlationId, e);
      throw new TranscriptionException("Transcription failed: " + e.getMessage(), e);
    }
  }
}
