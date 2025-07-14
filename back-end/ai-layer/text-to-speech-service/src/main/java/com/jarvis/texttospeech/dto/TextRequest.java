package com.jarvis.texttospeech.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TextRequest {
  @Size(min = 1, message = "Text cannot be empty if provided")
  private String text;

  private String ssml;
  private String voice;
}
