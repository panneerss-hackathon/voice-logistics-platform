package com.jarvis.missingfield.config;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "missing-field-config")
public class MissingFieldConfig {

  private Map<String, SchemaFields> schemas;
  private Map<String, String> synonyms;
  private Map<String, String> prompts;
  private Map<String, String> validations;

  @Data
  public static class SchemaFields {
    private List<String> required;
    private List<String> optional;
    private Map<String, String> conditional;
  }
}
