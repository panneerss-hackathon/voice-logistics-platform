package com.jarvis.speechtotext.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Slf4j
@Configuration
public class MultipartConfig {

  @Bean
  public MultipartConfigElement multipartConfigElement() {
    log.info("Configuring multipart file size limits to 10MB");
    var factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.ofMegabytes(10));
    factory.setMaxRequestSize(DataSize.ofMegabytes(10));
    return factory.createMultipartConfig();
  }
}
