package com.jarvis.texttospeech.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.speech")
@Slf4j
public class AzureTTSConfig {
  private String key;
  private String region;
  private String voice;
  private String outputFormat;

  public AzureTTSConfig() {
    log.info("✅ AzureTTSConfig loaded");
  }
}
