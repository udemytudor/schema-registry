# Avro Schema Registry Compatibility Exercise Solutions

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

### Solution:

**First Change:** we need to add the user phone to our model. It will be represented as a simple string.

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
    },
    {
      "name": "phoneNumber",
      "type": [
        "null",
        "string"
      ],
      "default": null
    }
  ]
}
```

**Second Change:** to meet compliance requirements, the system must now track whether the user has agreed to terms and conditions. If no information is available, we should assume they chose to not agree.

```json
{
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
    },
    {
      "default": null,
      "name": "phoneNumber",
      "type": [
        "null",
        "string"
      ]
    },
    {
      "default": false,
      "name": "agreedToTerms",
      "type": "boolean"
    }
  ],
  "name": "User",
  "namespace": "com.example",
  "type": "record"
}
```

**Third Change:** we moved the "name" field into a different model that encompasses more details about the user; we should delete this field from the User model.

```json
{
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
      "default": null,
      "name": "phoneNumber",
      "type": [
        "null",
        "string"
      ]
    },
    {
      "default": false,
      "name": "agreedToTerms",
      "type": "boolean"
    }
  ],
  "name": "User",
  "namespace": "com.example",
  "type": "record"
}
```

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

### Solution:

**First Change:** we need to include the paymentMethod field for tracking payment details, using an enum with values "CARD" and "GOOGLE_PAY".

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
    },
    {
      "name": "paymentMethod",
      "type": {
        "type": "enum",
        "name": "PaymentMethod",
        "symbols": [
          "CARD",
          "GOOGLE_PAY"
        ]
      }
    }
  ]
}
```

**Second Change:** the order amount will be streamed through our systems as part of a different data model. We want to
remove it from our Order model.

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
      "type": "double",
      "default": 0.0,
      "doc": "DEPRECATED: This field is no longer relevant for this data model."
    },
    {
      "name": "paymentMethod",
      "type": {
        "type": "enum",
        "name": "PaymentMethod",
        "symbols": [
          "CARD",
          "GOOGLE_PAY"
        ]
      }
    }
  ]
}
```