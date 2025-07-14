# Regenerate README.md including the entire user's requested section with detailed formatting

full_readme = """# 🎙️ Speech-to-Text Service (Java + Spring Boot)

This is a Spring Boot microservice that receives audio files via a REST API, transcribes them using **Azure Speech-to-Text**, and returns structured results like the transcription text, confidence score, duration, and language.

---

## 🚀 Features

- Accepts audio files in `.wav`, `.mp3`, `.m4a` formats
- Sends audio to Azure Cognitive Speech Service
- Returns transcription text and metadata
- Validates and restricts file size (10 MB max)
- Logs all steps with support for `X-Correlation-ID` tracking
- Global exception handling

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3.5.3
- Lombok
- Azure Cognitive Services (Speech-to-Text)
- Swagger (via SpringDoc)
- Maven

---

## ⚙️ Configuration

Edit the `application.yml` with your Azure credentials and region:

```yaml
server:
  port: 8081

azure:
  speech:
    key: YOUR_AZURE_SPEECH_KEY
    region: YOUR_REGION
    language: en-US
    endpoint-template: https://{region}.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1
    content-type: audio/wav
    accept: application/json
    user-agent: SpringBoot-STT-Service

## 📦 How to Build & Run

### 1. Clone and build
git clone https://github.com/your-org/speech-to-text-service.git
cd speech-to-text-service
mvn clean install

## 📦  Run locally
mvn spring-boot:run

## 📦  API Usage
Endpoint:
POST /api/speech/transcribe
Content-Type: multipart/form-data
Header: X-Correlation-ID (optional)

curl:
curl -X POST http://localhost:8081/api/speech/transcribe \
  -H "X-Correlation-ID: test-1234" \
  -F "audio=@/path/to/audio.wav"

postman:
Method: POST

URL: http://localhost:8081/api/speech/transcribe

Headers:
Content-Type: multipart/form-data
X-Correlation-ID: abc-xyz-uuid (optional)
Body: form-data
Key: audio (type: File)
Value: Upload your .wav or .mp3 file

## 📦  Sample responses

Success:
{
  "text": "Hello, this is a test message.",
  "duration": "unknown",
  "confidence": 0.9,
  "language": "en-US",
  "status": "SUCCESS"
}

## 🧾 Logging & Tracing
INFO  [Correlation-ID: abc123]: Received file '/tmp/audio.wav'
INFO  [Correlation-ID: abc123]: Transcription successful

## 🔍 Swagger UI
http://localhost:8081/swagger-ui.html