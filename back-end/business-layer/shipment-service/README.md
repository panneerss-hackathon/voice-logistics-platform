# 📦 shipment-service

This microservice handles **shipment creation**, **partial updates**, and **immutable audit history tracking**.  
It's designed for use in AI-powered voice assistant platforms and scalable logistics systems.

---

## ✅ Features

- 🆕 Create shipment entries
- ✏️ Partially update only changed fields
- 🔄 All changes are stored as immutable events (event sourcing)
- 📜 Full shipment history via `/history` endpoint
- 🧾 Correlation ID support for tracing across services
- 📖 OpenAPI docs (Swagger UI)
- ❤️ Health check via Spring Boot Actuator
- ⚙️ Configurable required/allowed fields via `application.yaml`

---

## 🚀 Endpoints

### 1. Create Shipment
**POST** `/api/shipments`

```json
{
  "senderName": "John",
  "senderAddress": "123 Main St",
  "receiverName": "Jane",
  "receiverAddress": "456 Park Ave",
  "packageWeight": 3.5,
  "deliveryType": "express"
}
```

**Response:**
```json
{
  "trackingNumber": "TRK-A1B2C3D4",
  "status": "CREATED",
  "message": "Shipment created",
  "correlationId": "abc-123..."
}
```

---

### 2. Update Shipment (Partial)
**PATCH** `/api/shipments/{trackingNumber}`

Send only the fields you want to change.

```json
{
  "receiverAddress": "789 New Street"
}
```

---

### 3. Get Shipment History
**GET** `/api/shipments/{trackingNumber}/history`

**Response:**
```json
{
  "trackingNumber": "TRK-12345678",
  "history": [
    {
      "eventType": "CREATED",
      "status": "CREATED",
      "eventTime": "2025-07-14T10:45:00",
      "snapshot": {
        "senderName": "...",
        "receiverAddress": "...",
        "packageWeight": 2.0,
        ...
      }
    },
    {
      "eventType": "UPDATED",
      "status": "UPDATED",
      "eventTime": "2025-07-14T11:05:00",
      "snapshot": {
        "receiverAddress": "789 New Street",
        ...
      }
    }
  ],
  "correlationId": "abc-123..."
}
```

---

### 4. Health Check
**GET** `/actuator/health`

```json
{ "status": "UP" }
```

---

### 5. Swagger UI
**URL:** `http://localhost:8083/swagger-ui.html`

Try the API directly from your browser.

---

## 🧠 Design Pattern

- ✅ **Event Sourcing**: Each create/update is stored as a separate event.
- ✅ **Immutable Records**: No deletes or in-place updates.
- ✅ **Snapshot Model**: You get the full state at each point in time.
- ✅ **Partial Update Logic**: Missing fields are auto-filled from the latest event.

---

## ⚙️ Configuration

Update field-level requirements in `src/main/resources/application.yaml`:

```yaml
entity-config:
  cases:
    CreateShipment:
      allowedFields:
        - sender_name
        - receiver_name
        - package_weight
        - ...
      requiredFields:
        - sender_name
        - receiver_name
        - package_weight
        - delivery_type
```

---

## 🛠️ Run Locally

### Prerequisites:
- Java 21+
- Maven 3.8+
- SQL Server or any JDBC-compatible DB

### Commands:
```bash
mvn clean package
java -jar target/shipment-service-1.0.0.jar
```

Then open: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

---

## 🧪 Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Data JPA
- Azure SQL Server
- Lombok
- OpenAPI via SpringDoc
- Maven

---

## 📁 Folder Structure

```
shipment-service/
├── src/
│   └── main/
│       ├── java/com/jarvis/shipment/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── exception/
│       │   ├── repository/
│       │   ├── service/
│       │   └── ShipmentServiceApplication.java
│       └── resources/
│           └── application.yaml
├── pom.xml
└── README.md
```

---

## 🔐 Tracing with Correlation ID

Every API request can optionally include:
```
x-correlation-id: <uuid>
```

It will be echoed back in the response and used in server logs for debugging across services.

---

## ✨ Built for Voice AI Hackathon

This service integrates seamlessly with:
- `speech-to-text-service`
- `intent-detection-service`
- `missing-field-service`
- Frontend via mobile or web (React Native, Angular, etc.)

---

## 📬 Need Help?

Contact the project maintainer: `dev@jarvis.ai`

Or raise an issue in the GitHub repository (if hosted).

---

**Happy Shipping!** 🚛
