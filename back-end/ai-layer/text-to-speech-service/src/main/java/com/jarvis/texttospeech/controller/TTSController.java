package com.jarvis.texttospeech.controller;

import com.jarvis.texttospeech.dto.TextRequest;
import com.jarvis.texttospeech.service.TextToSpeechService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
@Slf4j
public class TTSController {

  private final TextToSpeechService textToSpeechService;

  @PostMapping
  public ResponseEntity<?> synthesize(
      @Valid @RequestBody TextRequest request,
      @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
      @RequestParam(defaultValue = "false") boolean returnBuffer) {
    log.info(
        "📥 TTS Request received | correlationId={} | buffer={} | voice={}",
        correlationId,
        returnBuffer,
        request.getVoice());

    if (returnBuffer) {
      byte[] audio =
          textToSpeechService.synthesizeToBuffer(
              request.getText(), request.getSsml(), correlationId, request.getVoice());

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"tts.mp3\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(new ByteArrayResource(audio));
    } else {
      String audioUrl =
          textToSpeechService.synthesizeAndStore(
              request.getText(), request.getSsml(), correlationId, request.getVoice());

      return ResponseEntity.ok(Map.of("audioUrl", audioUrl));
    }
  }
}
