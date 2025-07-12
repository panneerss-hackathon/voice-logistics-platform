package com.jarvis.speechtotext.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TranscriptionResponse {
  private String text;
  private String duration;
  private double confidence;
  private String language;
  private String status;
}
