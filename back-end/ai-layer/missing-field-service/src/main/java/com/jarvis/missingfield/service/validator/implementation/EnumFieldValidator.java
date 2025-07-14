package com.jarvis.missingfield.service.validator.implementation;

import com.jarvis.missingfield.service.validator.FieldValidator;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EnumFieldValidator implements FieldValidator {
  @Override
  public boolean supports(String type) {
    return false; // Handled by service
  }

  @Override
  public Optional<String> validate(String field, Object value) {
    return Optional.empty();
  }
}
