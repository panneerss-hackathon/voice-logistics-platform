package com.jarvis.texttospeech.service;

public interface TextToSpeechService {
  byte[] synthesizeToBuffer(String text, String ssml, String correlationId, String voiceOverride);

  String synthesizeAndStore(String text, String ssml, String correlationId, String voiceOverride);
}
