# Avro Schema Registry Compatibility Exercises

## Compatibility Modes Reference Table

| Compatibility Mode | Allowed Operations                       |
|--------------------|------------------------------------------|
| `BACKWARD`         | - Delete fields<br>- Add optional fields |
| `FORWARD`          | - Add fields<br>- Delete optional fields |

## Exercise 1: BACKWARD Compatibility

**Initial Schema:**

```json
{
  "type": "record",
  "name": "User",
  "namespace": "com.example",
  "fields": [
    {
      "name": "userId",
      "type": "string"
    },
    {
      "name": "email",
      "type": "string"
    },
    {
      "name": "name",
      "type": "string"
    }
  ]
}
```

**Task:**

1. First change: We need to add the user phone to our model. It will be represented as a simple string.
2. Second change: To meet compliance requirements, the system must now track whether the user has agreed to terms and
   conditions. If no information is available, we should assume they chose to not agree.
3. Third change: We moved the "name" field into a different model that encompasses more details about the user; we
   should delete this field from the User model.

While maintaining `BACKWARD` compatibility. Remember that with `BACKWARD` compatibility, new consumers must be able to
read data produced with older schemas.

## Exercise 2: FORWARD_TRANSITIVE Compatibility

**Initial Schema:**

```json
{
  "type": "record",
  "name": "Order",
  "namespace": "com.example",
  "fields": [
    {
      "name": "orderId",
      "type": "string"
    },
    {
      "name": "orderDate",
      "type": "string"
    },
    {
      "name": "orderAmount",
      "type": "double"
    }
  ]
}
```

**Task:**

1. First change: We need to include the paymentMethod field for tracking payment details, using an enum with values "
   CARD" and "GOOGLE_PAY".
2. Second change: The order amount will be streamed through our systems as part of a different data model. We want to
   remove it from our Order model.

While maintaining `FORWARD_TRANSITIVE` compatibility. Remember that with `FORWARD_TRANSITIVE` compatibility, old
consumers must be able to read data produced with newer schemas, and this must hold true across all schema versions.