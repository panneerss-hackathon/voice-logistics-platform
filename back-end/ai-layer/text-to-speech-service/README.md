# 🔊 Text-to-Speech Service (Java + Spring Boot)

A Spring Boot microservice that receives text or SSML payload via a REST API, converts it into MP3 audio using **Azure Text-to-Speech**, and returns either the audio stream or a downloadable URL.

---

## 🚀 Features

- Accepts plain text or SSML input
- Supports dynamic voice selection (e.g., `en-IN-NeerjaNeural`, `en-US-JennyNeural`)
- Returns MP3 audio as a stream or public URL
- Logs every step with support for `X-Correlation-ID` tracking
- Global exception handling
- Swagger UI integrated

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3.5.3
- Azure Cognitive Services (Text-to-Speech)
- SpringDoc / Swagger UI
- Maven
- Lombok
- Spotless + Checkstyle
- Docker-ready

---

## ⚙️ Configuration

Edit `application.yml` or use environment variables for credentials:

```yaml
server:
  port: 8080

azure:
  speech:
    key: ${AZURE_SPEECH_KEY}
    region: ${AZURE_SPEECH_REGION}
    voice: en-US-JennyNeural
    output-format: audio-16khz-32kbitrate-mono-mp3

logging:
  level:
    root: INFO
    com.jarvis.texttospeech: DEBUG
```

---

## 📦 How to Build & Run

### 1. Clone and Build

```bash
git clone https://github.com/your-org/text-to-speech-service.git
cd text-to-speech-service
mvn clean install
```

### 2. Run Locally

```bash
AZURE_SPEECH_KEY=your_key \
AZURE_SPEECH_REGION=your_region \
mvn spring-boot:run
```

---

## 📡 API Usage

### ➤ Endpoint

```
POST /api/tts?returnBuffer=true|false
Content-Type: application/json
Header: X-Correlation-ID (optional)
```

### ➤ Payload Examples

#### 📝 Basic Text

```json
{
  "text": "Hello from Azure Text to Speech!"
}
```

#### 🗣️ SSML with Voice Style

```json
{
  "ssml": "<speak version='1.0'><voice name='en-IN-NeerjaNeural'>Hello!</voice></speak>"
}
```

#### 🎙️ With Custom Voice

```json
{
  "text": "This uses a specific voice.",
  "voice": "en-GB-RyanNeural"
}
```

---

## 🧪 cURL Examples

### 🔁 Return MP3 as binary stream

```bash
curl -X POST "http://localhost:8080/api/tts?returnBuffer=true" \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-4567" \
  -d '{ "text": "Your shipment is confirmed." }' \
  --output shipment.mp3
```

### 🔗 Return MP3 URL (stubbed or from blob)

```bash
curl -X POST "http://localhost:8080/api/tts" \
  -H "Content-Type: application/json" \
  -d '{ "text": "You have a new delivery.", "voice": "en-IN-NeerjaNeural" }'
```

---

## 📦 Sample Response (URL Mode)

```json
{
  "audioUrl": "https://storage.example.com/audio/123456789.mp3"
}
```

---

## 🧾 Logging & Tracing

```log
INFO  [Correlation-ID: test-4567] → Received TTS request with voice='en-US-JennyNeural'
INFO  [Correlation-ID: test-4567] → Synthesized MP3 output successfully
```

---

## 🔍 Swagger UI

Available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🩺 Health & Metrics

```
/actuator/health
/actuator/metrics
```

---

## 📄 License

MIT