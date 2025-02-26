# QR Code Generation and Payment API

Welcome to the QR Code Generation and Payment API! This project provides a straightforward payment system that enables you to:

- **Generate QR codes** for payments.
- **Process payments** upon scanning these QR codes.
- **Manage user and merchant balances** within a database.

## Prerequisites

Before diving in, ensure you have the following tools installed:

- **Java 17 or higher**: The core language for this project.
- **Maven**: For managing project dependencies.
- **IntelliJ IDEA**: Recommended IDE for development.
- **Postman**: Useful for testing API endpoints.

## Getting Started

Follow these steps to set up and run the project:

1. **Clone the Repository**: Begin by cloning the project to your local machine.
   ```bash
   git clone https://github.com/agiobudein/qrCodeGeneration.git
   cd qrCodeGeneration
   ```

2. **Open in IntelliJ IDEA**: Launch IntelliJ and open the cloned project.

3. **Build the Project**: Use Maven to build the project and resolve dependencies.
   ```bash
   ./mvnw clean install
   ```

4. **Run the Application**: Start the application using your IDE's run configuration or via Maven.
   ```bash
   ./mvnw spring-boot:run
   ```

The application should now be running locally on `http://localhost:8080`.

## API Endpoints

### 1. Generate QR Code for Payment

**Endpoint**: `POST /api/payments/generate`

**Description**: Creates a QR code for a specified payment amount and associates it with a merchant.

**Request Body**:
```json
{
  "merchantId": "string",
  "amount": 0.0
}
```

**Response**:
```json
{
  "qrCodeId": "string",
  "qrCodeImage": "base64EncodedString"
}
```

### 2. Process Payment via QR Code

**Endpoint**: `POST /api/payments/process`

**Request Body**:
```json
{
  "qrCodeId": "string",
  "userId": "string"
}
```

**Response**:
```json
{
  "status": "string",
  "message": "string"
}
```

### 3. Check Balance

**Endpoint**: `GET /api/users/{userId}/balance`

**Response**:
```json
{
  "userId": "string",
  "balance": 0.0
}
```

## Testing the API

You can test the API using Postman:

1. **Generate QR Code** (`POST http://localhost:8080/api/payments/generate`)
2. **Process Payment** (`POST http://localhost:8080/api/payments/process`)
3. **Check Balance** (`GET http://localhost:8080/api/users/{userId}/balance`)

For more details, visit the [GitHub repository](https://github.com/agiobudein/qrCodeGeneration).
