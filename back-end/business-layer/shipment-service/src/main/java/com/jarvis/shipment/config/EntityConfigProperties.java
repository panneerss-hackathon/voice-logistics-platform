package com.jarvis.shipment.config;

import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "entity-config")
public class EntityConfigProperties {
  private Map<String, ShipmentCase> cases;
  private double confidenceThreshold;

  @Data
  public static class ShipmentCase {
    private Set<String> allowedFields;
    private Set<String> requiredFields;
  }
}
