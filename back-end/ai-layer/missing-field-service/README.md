# 🚚 Missing Field Service

A Spring Boot microservice for conversational logistics applications.  
Given an intent (such as CreateShipment, TrackShipment, etc.) and currently extracted fields, this service tells you **which required fields are missing, which are invalid, and what user prompts to show**—enabling intelligent, multi-turn shipment workflows in voice or text bots.

---

## 🌟 Features

- Supports all core business use cases: **CreateShipment, TrackShipment, ReturnShipment, RescheduleDelivery, ReportIssue**
- YAML-driven required/optional/conditional field schemas per intent
- Extensible validation for field type/format (e.g. phone, enum, positive number)
- Entity normalization: handles synonyms (e.g. “recipient” → “receiver_name”)
- Returns user-friendly, context-aware prompts for missing/invalid fields
- Tracks progress (filled/total/percent) in the conversation flow
- Global exception handling, correlation ID support for distributed tracing
- Well-structured, production-ready Java code with logging and error handling

---

## ⚙️ Configuration

- All logic is in `application.yaml`: update required/optional fields, add prompts, validations, or synonyms—**no code change needed**.
- Example intent schemas:
    - `CreateShipment`
    - `TrackShipment`
    - `ReturnShipment`
    - `RescheduleDelivery`
    - `ReportIssue`

---

## 📦 REST API

### `POST /missing-fields`

**Request Example:**
```json
{
  "intentType": "CreateShipment",
  "extractedFields": {
    "sender_name": "Alice",
    "receiver_name": "",
    "sender_phone": "9123456789",
    "delivery_type": "standard"
  }
}
Response Example:

json
Copy
Edit
{
  "missingFields": [
    "sender_address",
    "receiver_name",
    "receiver_address",
    "receiver_phone",
    "package_weight",
    "package_description"
  ],
  "fieldPrompts": {
    "sender_address": "What is the sender's address?",
    "receiver_name": "Who is the receiver?",
    "receiver_address": "What is the receiver's address?",
    "receiver_phone": "Receiver's phone number?",
    "package_weight": "How much does the package weigh (in kg)?",
    "package_description": "What are you sending?"
  },
  "validationErrors": {},
  "progress": {
    "filled": 3,
    "total": 9,
    "percent": 33
  },
  "dialogAct": "re-prompt"
}
🏁 Running & Testing
Build:
mvn clean install

Run:
java -jar target/missing-field-service-1.0.0.jar

Try it out:

Swagger UI at http://localhost:8080/swagger-ui.html

Or send POST requests to http://localhost:8080/missing-fields

📂 Project Structure
css
Copy
Edit
src/main/java/com/jarvis/missingfield
├── config/
├── controller/
├── dto/
├── exception/
├── service/
│   └── validator/
└── MissingFieldServiceApplication.java
src/main/resources/application.yaml
pom.xml
README.md
💡 Extending
Add new business cases:
Just add a new intent schema in application.yaml under schemas:.

Add new validations:
Implement the FieldValidator interface in Java and Spring will auto-wire it.