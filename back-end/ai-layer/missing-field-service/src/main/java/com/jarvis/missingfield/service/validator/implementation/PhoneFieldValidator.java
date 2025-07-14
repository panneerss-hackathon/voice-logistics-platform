package com.jarvis.missingfield.service.validator.implementation;

import com.jarvis.missingfield.service.validator.FieldValidator;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PhoneFieldValidator implements FieldValidator {
  @Override
  public boolean supports(String type) {
    return "phone".equals(type);
  }

  @Override
  public Optional<String> validate(String field, Object value) {
    if (value == null || !value.toString().matches("\\d{10}")) {
      return Optional.of("Invalid or missing " + field + ". Enter a 10-digit phone number.");
    }
    return Optional.empty();
  }
}
