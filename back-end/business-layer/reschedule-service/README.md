
# 📦 Reschedule Service (Spring Boot + Azure Service Bus)

This microservice allows users to **reschedule a shipment delivery** by submitting a tracking number, new delivery date, and new delivery time.

It saves each request as an immutable event in the database and publishes a `RESCHEDULED` event to **Azure Service Bus** for downstream processing (e.g., tracking).

---

## 🚀 Features

- Accepts shipment rescheduling requests via REST API
- Stores immutable `RescheduleEvent` entries in the database
- Publishes `RESCHEDULED` event to `shipment-events` topic on Azure Service Bus
- Includes correlation ID support for tracing across services
- Follows clean architecture: controller → service → repository
- Exposes Swagger/OpenAPI documentation

---

## 📘 API Specification

### `POST /reschedule`

Reschedules a shipment.

#### ✅ Request Body (application/json)
```json
{
  "trackingNumber": "TRK-ABC12345",
  "newDeliveryDate": "2025-07-21",
  "newDeliveryTime": "15:00:00"
}
```

#### 🔄 Response
```json
{
  "message": "Reschedule request received",
  "trackingNumber": "TRK-ABC12345",
  "status": "RESCHEDULED",
  "correlationId": "b27aee2e-b2dd-41aa-8d03-abc12345xyz"
}
```

#### 🔐 Headers
- `x-correlation-id`: Optional string for traceability

---

## 📨 Azure Service Bus Event

After saving the event, the service **publishes a message** to the topic `shipment-events`.

#### Event Format
```json
{
  "trackingNumber": "TRK-ABC12345",
  "eventType": "RESCHEDULED",
  "status": "RESCHEDULED",
  "newDeliveryDate": "2025-07-21",
  "newDeliveryTime": "15:00:00",
  "eventTime": "2025-07-14T12:30:00",
  "correlationId": "b27aee2e-b2dd-41aa-8d03-abc12345xyz"
}
```

---

## 🗃️ Database Table: `reschedule_event`

| Column            | Type        | Description                     |
|-------------------|-------------|---------------------------------|
| id                | UUID        | Primary key                     |
| tracking_number   | VARCHAR     | Shipment tracking number        |
| new_delivery_date | DATE        | New scheduled delivery date     |
| new_delivery_time | TIME        | New scheduled delivery time     |
| event_type        | VARCHAR     | Always `RESCHEDULED`            |
| status            | VARCHAR     | Always `RESCHEDULED`            |
| event_time        | TIMESTAMP   | Time of event creation          |
| correlation_id    | VARCHAR     | ID used for end-to-end tracing  |

---

## ⚙️ Configuration (application.yaml)

```yaml
server:
  port: 8086

azure:
  servicebus:
    connection-string: ${AZURE_SERVICE_BUS_CONNECTION}
    topic: shipment-events
```

---

## 📦 Tech Stack

- Java 21
- Spring Boot 3.5.3
- Spring Data JPA
- Azure Service Bus JMS
- Lombok
- Swagger (SpringDoc)
- Maven
- SQL Server (JDBC)

---

## 🧪 Test It

Once deployed, test using tools like:
- Postman
- curl
- Swagger UI (`/swagger-ui.html`)

---

## 📈 Future Enhancements

- Add status update workflow (e.g., PENDING → RESCHEDULED)
- Allow multiple reschedules and track history
- Implement retry on failed JMS send
