package com.jarvis.texttospeech.service.impl;

import com.jarvis.texttospeech.config.AzureTTSConfig;
import com.jarvis.texttospeech.exception.TextToSpeechException;
import com.jarvis.texttospeech.service.TextToSpeechService;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSpeechServiceImpl implements TextToSpeechService {

  private final AzureTTSConfig config;

  @Override
  public byte[] synthesizeToBuffer(
      String text, String ssml, String correlationId, String voiceOverride) {
    log.debug("▶️ synthesizeToBuffer() called | correlationId={}", correlationId);

    try {
      SpeechConfig speechConfig =
          SpeechConfig.fromSubscription(config.getKey(), config.getRegion());
      String selectedVoice =
          (voiceOverride != null && !voiceOverride.isBlank()) ? voiceOverride : config.getVoice();
      speechConfig.setSpeechSynthesisVoiceName(selectedVoice);
      speechConfig.setSpeechSynthesisOutputFormat(
          SpeechSynthesisOutputFormat.valueOf(config.getOutputFormat()));

      AudioConfig audioConfig = AudioConfig.fromDefaultSpeakerOutput();
      SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, audioConfig);

      SpeechSynthesisResult result;

      if (ssml != null && !ssml.isBlank()) {
        result = synthesizer.SpeakSsmlAsync(ssml).get();
        log.info(
            "🔊 SSML synthesis complete | voice={} | correlationId={}",
            selectedVoice,
            correlationId);
      } else if (text != null && !text.isBlank()) {
        result = synthesizer.SpeakTextAsync(text).get();
        log.info(
            "🗣️ Text synthesis complete | voice={} | correlationId={}",
            selectedVoice,
            correlationId);
      } else {
        throw new TextToSpeechException("Either 'text' or 'ssml' must be provided.");
      }

      if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
        return result.getAudioData();
      } else {
        throw new TextToSpeechException("Azure TTS failed: " + result.getReason());
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // ✅ properly re-interrupt
      log.error("🚨 Thread interrupted during TTS synthesis | correlationId={}", correlationId);
      throw new TextToSpeechException("TTS operation was interrupted", e);
    } catch (ExecutionException e) {
      log.error(
          "🔥 Azure SDK execution failed | correlationId={} | cause={}",
          correlationId,
          e.getCause(),
          e);
      throw new TextToSpeechException("Azure SDK execution error", e.getCause());
    } catch (Exception e) {
      log.error(
          "❌ Unexpected error during TTS | correlationId={} | message={}",
          correlationId,
          e.getMessage(),
          e);
      throw new TextToSpeechException("Unexpected TTS error", e);
    }
  }

  @Override
  public String synthesizeAndStore(
      String text, String ssml, String correlationId, String voiceOverride) {
    String key = String.valueOf((text != null ? text : ssml).hashCode());
    log.warn(
        "📁 synthesizeAndStore() not implemented — returning dummy URL | correlationId={}",
        correlationId);
    return "https://storage.example.com/audio/" + key + ".mp3";
  }
}
