package com.jarvis.missingfield;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MissingFieldServiceApplication {
  public static void main(String[] args) {
      log.info("Starting MissingFieldServiceApplication...");
      SpringApplication.run(MissingFieldServiceApplication.class, args);
      log.info("MissingFieldServiceApplication started successfully.");
  }
}