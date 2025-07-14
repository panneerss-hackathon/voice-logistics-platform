package com.jarvis.missingfield.service.validator;

import java.util.Optional;

public interface FieldValidator {
  boolean supports(String validationType);

  Optional<String> validate(String field, Object value);
}
