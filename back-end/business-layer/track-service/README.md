# 📦 Tracking Service (Java + Spring Boot)

This microservice receives shipment events via Azure Service Bus and stores tracking history for each package. It exposes APIs to retrieve the latest tracking status and full event history for a given tracking number.

---

## 🚀 Features

- 📨 Listens to `shipment-events` on Azure Service Bus
- 📥 Persists tracking events in SQL Server
- 🔍 REST APIs to fetch latest status or full tracking history
- 🧾 Each event includes: `trackingNumber`, `eventType`, `status`, `timestamp`, and `correlationId`
- 📊 Supports Swagger UI for interactive API exploration
- 📌 Designed for integration with voice-based shipment assistant

---

## 🛠️ Tech Stack

| Layer             | Technology                          |
|------------------|--------------------------------------|
| Language          | Java 21                              |
| Framework         | Spring Boot 3.5.3                    |
| Messaging         | Azure Service Bus (JMS)              |
| Persistence       | Spring Data JPA + SQL Server         |
| API Docs          | SpringDoc OpenAPI (Swagger)          |
| Logging           | SLF4J + Correlation ID support       |
| Validation        | Jakarta Bean Validation              |
| Dev Tools         | Spotless, Checkstyle, Enforcer       |

---

## 📬 Event Format (JMS JSON Payload)

```json
{
  "trackingNumber": "SHIP12345678",
  "eventType": "DELIVERED",
  "status": "Package successfully delivered",
  "correlationId": "abcd-1234"
}
```

---

## 📡 API Endpoints

| Method | Endpoint                            | Description                    |
|--------|-------------------------------------|--------------------------------|
| GET    | `/api/tracking/{trackingNumber}`    | Get latest tracking event      |
| GET    | `/api/tracking/{trackingNumber}/history` | Get full event history     |

> Base path: `http://<host>:8084`

---

## 🔧 Configuration (`application.yaml`)

```yaml
server:
  port: 8084

azure:
  servicebus:
    connection-string: ${AZURE_SERVICE_BUS_CONNECTION}
    topic: shipment-events

spring:
  datasource:
    url: jdbc:sqlserver://<YOUR_SQL_HOST>:1433;databaseName=tracking_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 🧪 Run Locally

```bash
# Set env vars
export AZURE_SERVICE_BUS_CONNECTION="<your-conn-string>"
export DB_USERNAME=sa
export DB_PASSWORD=yourStrong(!)Password

# Run the service
./mvnw spring-boot:run
```

---

## 📜 License

This project is part of the **AI Voice Shipment Assistant** system under the `com.jarvis` namespace. For internal and demo use only.

---