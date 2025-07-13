package com.jarvis.missingfield.service.validator.implementation;

import com.jarvis.missingfield.service.validator.FieldValidator;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BooleanFieldValidator implements FieldValidator {
  @Override
  public boolean supports(String type) {
    return "boolean".equals(type);
  }

  @Override
  public Optional<String> validate(String field, Object value) {
    if (value == null) return Optional.of(field + " must be true or false.");
    String val = value.toString().toLowerCase();
    if (!val.equals("true") && !val.equals("false")) {
      return Optional.of(field + " must be true or false.");
    }
    return Optional.empty();
  }
}
