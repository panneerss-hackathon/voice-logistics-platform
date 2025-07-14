# 📦 Return Service

This microservice handles return requests for shipments in a logistics system. When a return is submitted, the service stores the return event and publishes a `RETURNED` event to Azure Service Bus.

---

## ✅ API: `POST /returns`

### Request Body
```json
{
  "orderId": "ORD123456",
  "reason": "Wrong item delivered",
  "receiverAddress": "456 New Street, California"
}
```

### Headers
- `x-correlation-id` (Optional): Used for tracing logs and event flows.

---

## ✅ Success Response
```json
{
  "message": "Return request accepted",
  "orderId": "ORD123456",
  "correlationId": "c67f3c14-df90-4f83-8a3f-ef7f25dc1cf9",
  "status": "RETURNED",
  "timestamp": "2025-07-14T22:34:56"
}
```

---

## 🔴 Error Response Examples

### Validation Failure
```json
{
  "error": "Invalid request",
  "details": [
    "orderId must not be blank",
    "reason must not be null"
  ]
}
```

### Internal Server Error
```json
{
  "error": "Unexpected error occurred",
  "traceId": "c67f3c14-df90-4f83-8a3f-ef7f25dc1cf9"
}
```

---

## 📨 Azure Service Bus Event (Published)

Event is sent to topic: `shipment-events`

### Example Payload
```json
{
  "eventType": "RETURNED",
  "orderId": "ORD123456",
  "receiverAddress": "456 New Street, CA",
  "reason": "Damaged item",
  "correlationId": "c67f3c14-df90-4f83-8a3f-ef7f25dc1cf9",
  "eventTime": "2025-07-14T22:34:56"
}
```

---

## 🧠 Design Highlights
- Uses `JmsTemplate` for event publishing.
- Events are persisted and published atomically.
- `x-correlation-id` tracks the full lifecycle of the return.
