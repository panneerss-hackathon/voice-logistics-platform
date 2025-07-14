package com.jarvis.tts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TextToSpeechServiceApplication {

  public static void main(String[] args) {
    log.info("🚀 Starting Text-to-Speech Service...");
    SpringApplication.run(TextToSpeechServiceApplication.class, args);
    log.info("🚀 Started Text-to-Speech Service...");
  }
}
