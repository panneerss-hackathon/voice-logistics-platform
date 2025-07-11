package com.azure.speech;

import java.io.File;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Value("${azure.speech.key}")
    private String azureSpeechKey;

    @Value("${azure.speech.region}")
    private String azureRegion;

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Save uploaded file to temp file
            File tempFile = File.createTempFile("uploaded-", ".wav");
            file.transferTo(tempFile);

            String result = transcribeWithAzure(tempFile.getAbsolutePath());

            // Delete temp file
            tempFile.delete();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String transcribeWithAzure(String audioFilePath) throws Exception {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(azureSpeechKey, azureRegion);
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFilePath);

        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

        Future<SpeechRecognitionResult> task = recognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();

        recognizer.close();
        audioConfig.close();
        speechConfig.close();

        if (result.getReason() == ResultReason.RecognizedSpeech) {
            return result.getText();
        } else {
            throw new RuntimeException("Recognition failed: " + result.getReason());
        }
    }
}

