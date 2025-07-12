package com.jarvis.speechtotext.service;

import com.jarvis.speechtotext.service.response.TranscriptionResponse;
import java.io.File;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SpeechToTextService {

  @Value("${azure.speech.key}")
  private String azureKey;

  @Value("${azure.speech.region}")
  private String azureRegion;

  @Value("${azure.speech.language}")
  private String language;

  @Value("${azure.speech.endpoint-template}")
  private String endpointTemplate;

  @Value("${azure.speech.content-type}")
  private String contentType;

  @Value("${azure.speech.accept}")
  private String acceptHeader;

  @Value("${azure.speech.user-agent}")
  private String userAgent;

  public TranscriptionResponse transcribeAudioFile(File file, String correlationId)
      throws Exception {
    var endpoint = endpointTemplate.replace("{region}", azureRegion);
    log.debug("Correlation-ID [{}]: Using endpoint {}", correlationId, endpoint);

    var headers = new HttpHeaders();
    headers.set("Ocp-Apim-Subscription-Key", azureKey);
    headers.set("Content-Type", contentType);
    headers.set("Accept", acceptHeader);
    headers.set("User-Agent", userAgent);

    if (correlationId != null && !correlationId.isBlank()) {
      headers.set("X-Correlation-ID", correlationId);
    }

    var audioBytes = Files.readAllBytes(file.toPath());
    var request = new HttpEntity<>(audioBytes, headers);

    var restTemplate = new RestTemplate();
    var response =
        restTemplate.exchange(
            endpoint + "?language=" + language, HttpMethod.POST, request, String.class);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      log.debug("Correlation-ID [{}]: Response received", correlationId);
      return new TranscriptionResponse(response.getBody(), "unknown", 0.9, language, "SUCCESS");
    } else {
      log.warn("Correlation-ID [{}]: No speech detected", correlationId);
      return new TranscriptionResponse("", "0s", 0.0, language, "NO_SPEECH_DETECTED");
    }
  }
}
