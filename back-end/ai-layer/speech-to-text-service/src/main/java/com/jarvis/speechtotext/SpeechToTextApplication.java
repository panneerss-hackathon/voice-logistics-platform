package com.jarvis.speechtotext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SpeechToTextApplication {

  public static void main(String[] args) {
    log.info("Starting SpeechToTextApplication...");
    SpringApplication.run(SpeechToTextApplication.class, args);
    log.info("SpeechToTextApplication started successfully.");
  }
}
