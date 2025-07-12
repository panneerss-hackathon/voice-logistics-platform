package com.jarvis.texttospeech.exception;

/** Custom exception for Text-to-Speech related errors. */
public class TextToSpeechException extends RuntimeException {
  public TextToSpeechException(String message) {
    super(message);
  }

  public TextToSpeechException(String message, Throwable cause) {
    super(message, cause);
  }
}
