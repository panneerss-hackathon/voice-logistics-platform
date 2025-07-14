package com.jarvis.missingfield.service.validator.implementation;

import com.jarvis.missingfield.service.validator.FieldValidator;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PositiveNumberValidator implements FieldValidator {
  @Override
  public boolean supports(String type) {
    return "positive_number".equals(type);
  }

  @Override
  public Optional<String> validate(String field, Object value) {
    try {
      double val = Double.parseDouble(value.toString());
      if (val <= 0) {
        return Optional.of(field + " must be positive.");
      }
    } catch (Exception e) {
      return Optional.of("Invalid " + field + ": must be a number.");
    }
    return Optional.empty();
  }
}
