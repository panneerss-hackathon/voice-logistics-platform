package com.jarvis.missingfield.dto;

import java.util.Map;
import lombok.Data;

@Data
public class MissingFieldRequest {
  private String intentType;
  private Map<String, Object> extractedFields;
  private Map<String, Object> fieldHistory; // For correction flows (optional)
  private String dialogAction; // (Optional) e.g., "correction"
}
