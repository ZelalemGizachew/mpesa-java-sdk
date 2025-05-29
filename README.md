# Java M-Pesa Payment SDK

[![CI Pipeline](https://github.com/dere7/mpesa-sdk/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dere7/mpesa-sdk/actions/workflows/ci-cd.yml)

This project integrates with the **Mpesa API**, providing functionality to trigger payments, validate them, register payment notification URLs, and handle USSD push requests and other requests.

## Live Documentation

🔗 [mpesa-java-docs-et.vercel.app/](https://mpesa-java-docs-et.vercel.app)

## 🚀 Features

### 🔒 Authentication

- Secure and fullly manged authentication and access token mangement.

### 📦 Data Models for Request

- Predefined, ready to use data models like `UssdPushRequest`, `RegisterUrlRequest`, `C2BPaymentValidationRequest` and more.

### ⚙️ Async and Sync API Client

- Supports both **synchronous** and **asynchronous** API calls for optimal performance.

### 💻 Custom HTTP Client

- Customizable http `Client` for making HTTP requests to the M-Pesa API.

### 🛡️ Validation

- Automatic validation for request integrity and error handling.

### 🧪 Testing

- Includes tests using **MockWebServer** for simulating API responses and also contain for actual integration tests.

## 🔧 Requirements

- Java 11 or higher.
- Maven for dependency management.
- Mpesa API credentials (consumer key, consumer secret).

## 🛠️ Installation

To integrate Mpesa Payment API into your project, follow these steps:

1. **Clone the repository**:

   ```bash
   git clone https://github.com/dere7/mpesa-sdk.git
   cd mpesa-sdk
   ```

2. **Install dependencies**:
   For Maven:
   ```bash
   mvn install -Dgroups=mocked # This runs only mock tests then install locally
   ```

## ⚡ Usage Examples

### Maven

```xml
<dependency>
  <groupId>io.github.dere7</groupId>
  <artifactId>mpesa-sdk</artifactId>
  <version>1.1</version>
</dependency>
```

### Example 1: Trigger USSD Push Request (Without Configuration)

```java
// Create Mpesa instance without configuration
Mpesa mpesa = new Mpesa("test_consumer_key", "test_consumer_secret");

UssdPushRequest request = UssdPushRequest.builder()
    .merchantRequestId("4354354351")
    .businessShortCode("174379")
    .passkey("passkey")
    .transactionType("CustomerPayBillOnline")
    .amount("10000")
    .partyA("600982")
    .partyB("600000")
    .phoneNumber("251708374149")
    .transactionDesc("Monthly Unlimited Package")
    .callBackUrl("https://example.com/callback")
    .accountReference("34q5125")
    .build();

UssdPushResponse response = mpesa.triggerUssdPush(request);
System.out.println(response.getResponseDescription());
```

### Example 2: Trigger USSD Push Request (With Configuration)

```java
// Create Mpesa instance with configuration
Configuration config = Configuration.builder()
    .baseUrl("https://apisandbox.safaricom.et")
    .logLevel(Level.ALL)
    .build();

Mpesa mpesa = new Mpesa("test_consumer_key", "test_consumer_secret", config);

UssdPushRequest request = UssdPushRequest.builder()
    .merchantRequestId("4354354351")
    .businessShortCode("174379")
    .password("password")
    .timestamp("20250104135017")
    .transactionType("CustomerPayBillOnline")
    .amount("10000")
    .partyA("600982")
    .partyB("600000")
    .phoneNumber("251708374149")
    .transactionDesc("Monthly Unlimited Package")
    .callBackUrl("https://example.com/callback")
    .accountReference("34q5125")
    .build();

UssdPushResponse response = mpesa.triggerUssdPush(request);
System.out.println(response.getResponseDescription());
```

### Example 3: Trigger USSD Push Request (Asynchronous)

```java
UssdPushRequest request = UssdPushRequest.builder()
    .merchantRequestId("4354354351")
    .businessShortCode("174379")
    .passkey("passkey")
    .transactionType("CustomerPayBillOnline")
    .amount("10000")
    .partyA("600982")
    .partyB("600000")
    .phoneNumber("251708374149")
    .transactionDesc("Monthly Unlimited Package")
    .callBackUrl("https://example.com/callback")
    .accountReference("34q5125")
    .build();

mpesa.triggerUssdPushAsync(request,
    response -> {
        System.out.println(response.getResponseDescription());
    },
    error -> {
        System.out.println("Error callback should not be invoked");
    });
```

### Example 4: Custom Client

- You can create your custom http client and use it to make requests to the API.

```java
// Create a custom client
public class ClientImpl implements Client {
  @Override
  	<T> ResponseClient sendSyncRequest(RequestClient<T> requestClient) {
      // YOUR IMPLEMENTATION HERE
    };

    @Override
    <T> void sendAsyncRequest(RequestClient<T> requestClient, CallbackClient callbackClient, CallbackErrorClient callbackErrorClient){
      // YOUR IMPLEMENTATION HERE
    }

}

// Create Mpesa instance with custom client

Mpesa mpesa = new Mpesa("test_consumer_key", "test_consumer_secret");
mpesa.setClient(new ClientImpl());
```

## 📚 Method Docs

### Available Methods

#### General Payment Operations

- **`triggerUssdPush`**
  Triggers a USSD push request (synchronous).

  ```java
  UssdPushResponse response = mpesa.triggerUssdPush(ussdPushRequest);
  ```

- **`registerPaymentNotificationUrl`**
  Registers a payment notification URL (synchronous).

  ```java
  RegisterUrlResponse response = mpesa.registerPaymentNotificationUrl(registerUrlRequest, apiKey);
  ```

- **`validatePayment`**
  Validates a payment (synchronous).

  ```java
  ValidationConfirmationResponse response = mpesa.validatePayment(validationRequest);
  ```

- **`confirmPayment`**
  Confirms a payment after validation.

  ```java
  ValidationConfirmationResponse response = mpesa.confirmPayment(validationRequest);
  ```

- **`simulatePayment`**
  simulate a transaction after validation.
  ```java
  SimulateResponse response = mpesa.simulatePayment(simulateRequest);
  ```

#### Transaction Operations

- **`payOut`**
  Performs a business-to-customer (B2C) payment (synchronous).

  ```java
  SuccessResponse response = mpesa.payOut(payOutRequest);
  ```

- **`transactionStatus`**
  Queries the status of a transaction (synchronous).

  ```java
  SuccessResponse response = mpesa.transactionStatus(transactionStatusRequest);
  ```

- **`transactionReversal`**
  Reverses a transaction (synchronous).

  ```java
  SuccessResponse response = mpesa.transactionReversal(transactionReversalRequest);
  ```

- **`accountBalance`**
  Queries the balance of the shortcode (synchronous).
  ```java
  SuccessResponse response = mpesa.accountBalance(accountBalanceRequest);
  ```

#### Asynchronous Methods

For each synchronous method, there is an equivalent asynchronous method. These accept success and error callbacks as parameters:

- **USSD Push**

  ```java
  mpesa.triggerUssdPushAsync(ussdPushRequest,
      response -> System.out.println("Success: " + response),
      error -> System.err.println("Error: " + error));
  ```

- **Payment Notification URL Registration**

  ```java
  mpesa.registerPaymentNotificationUrlAsync(registerUrlRequest, apiKey,
      response -> System.out.println("Success: " + response),
      error -> System.err.println("Error: " + error));
  ```

- **Payment Validation**

  ```java
  mpesa.validatePaymentAsync(validationRequest,
      response -> System.out.println("Validation successful."),
      error -> System.err.println("Validation failed."));
  ```

- **Simulate transaction**

  ```java
  SimulateResponse response = mpesa.simulatePayment(simulateRequest);
  mpesa.simulatePaymentAsync(simulateRequest,
      response -> System.out.println("initaited simulated transaction"),
      error -> System.err.println("cannot initiate simulated transaction."));
  ```

- **B2C Payment**

  ```java
  mpesa.payOutAsync(payOutRequest,
      response -> System.out.println("Payment successful."),
      error -> System.err.println("Payment failed."));
  ```

- **Transaction Status**

  ```java
  mpesa.transactionStatusAsync(transactionStatusRequest,
      response -> System.out.println("Transaction successful."),
      error -> System.err.println("Transaction query failed."));
  ```

- **Transaction Reversal**

  ```java
  mpesa.transactionReversalAsync(transactionReversalRequest,
      response -> System.out.println("Reversal successful."),
      error -> System.err.println("Reversal failed."));
  ```

- **Account Balance**
  ```java
  mpesa.accountBalanceAsync(accountBalanceRequest,
      response -> System.out.println("Balance fetched."),
      error -> System.err.println("Failed to fetch balance."));
  ```

### 🔧 Configuration Options

- **`connectionTimeout`**: The request timeout in milliseconds (default: `5000`).
- **`maxRetries`**: The number of times to retry a request in case of failure (default: `1`).
- **`retryOnConnectionFailure`**: Whether to retry a request in case of connection failure (default: `false`).
- **`baseUrl`**: The base URL of the M-Pesa API (default: `https://apisandbox.safaricom.et`).
- **`logLevel`**: Log level for selectively displaying logs (default: `Level.SEVERE`).

### 🛠️ Error Handling

- Errors are raised as `MpesaApiException` with descriptive messages for troubleshooting.

## 💡 Additional Features

- **Asynchronous Handling**: Supports non-blocking requests for better performance.
- **Logging**: Logs API requests and responses for debugging.
