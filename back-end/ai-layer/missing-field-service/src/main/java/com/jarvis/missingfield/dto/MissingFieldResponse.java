package com.jarvis.missingfield.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissingFieldResponse {
  private List<String> missingFields;
  private Map<String, String> fieldPrompts;
  private Map<String, String> validationErrors;
  private Map<String, Object> progress;
  private String dialogAct;
}
