package et.safaricom.transaction;

import et.safaricom.Mpesa;
import et.safaricom.commons.response.SuccessResponse;
import et.safaricom.config.Configuration;
import et.safaricom.constants.MediaType;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.transaction.request.AccountBalanceRequest;
import et.safaricom.transaction.request.PayOutRequest;
import et.safaricom.transaction.request.TransactionReversalRequest;
import et.safaricom.transaction.request.TransactionStatusRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

@Tag("mocked")
public class TransactionMockedTest {
    private static final String SUCCESS_RESPONSE = "{\n" +
            "    \"ConversationID\": \"AG_20240209_70205ca849aecd7fbd7f\",\n" +
            "    \"OriginatorConversationID\": \"77fd-4542-b4f4-1748deeeb48f\",\n" +
            "    \"ResponseCode\": \"0\",\n" +
            "    \"ResponseDescription\": \"Accept the service request successfully.\"\n" +
            "}";
    private static final String FAILURE_RESPONSE = "{\n" +
            "    \"requestId\": \"fc21-42cd-af6b-59613899fed5\",\n" +
            "    \"errorCode\": \"400.002.02\",\n" +
            "    \"errorMessage\": \"Bad Request - Invalid CommandID\"\n" +
            "}";

    private MockWebServer mockWebServer;
    private Mpesa mpesa;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        configureMpesa();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private void configureMpesa() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
                "   \"access_token\": \"mock_access_token\",\n" +
                "   \"token_type\": \"Bearer\",\n" +
                "   \"expires_in\": \"3599\"\n" +
                "}"));
        String baseUrl = mockWebServer.url("/").toString();

        Configuration config = Configuration.builder()
                .logLevel(Level.ALL)
                .baseUrl(baseUrl)
                .build();
        mpesa = new Mpesa("test_consumer_key", "test_consumer_secret", config);
    }

    @Test
    public void payOutSyncFail() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        PayOutRequest request = preparePayOutRequest();

        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesa.payOut(request));
        assertEquals("Bad Request - Invalid CommandID", exception.getMessage());
    }

    @Test
    public void payOutSyncSuccess() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        PayOutRequest request = preparePayOutRequest();

        SuccessResponse response = mpesa.payOut(request);
        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertEquals("Accept the service request successfully.", response.getResponseDescription());
    }

    @Test
    public void transactionStatusSyncFail() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        TransactionStatusRequest request = prepareTransactionStatusRequest("INVALID_ID");

        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesa.transactionStatus(request));
        assertEquals("Bad Request - Invalid CommandID", exception.getMessage());
    }

    @Test
    public void transactionStatusSyncSuccess() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        TransactionStatusRequest request = prepareTransactionStatusRequest("VALID_ID");

        SuccessResponse response = mpesa.transactionStatus(request);
        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertEquals("Accept the service request successfully.", response.getResponseDescription());
    }

    @Test
    public void accountBalanceSyncFail() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        AccountBalanceRequest request = prepareAccountBalanceRequest();

        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesa.accountBalance(request));
        assertEquals("Bad Request - Invalid CommandID", exception.getMessage());
    }

    @Test
    public void accountBalanceSyncSuccess() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        AccountBalanceRequest request = prepareAccountBalanceRequest();

        SuccessResponse response = mpesa.accountBalance(request);
        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertEquals("Accept the service request successfully.", response.getResponseDescription());
    }

    // ASYNC

    @Test
    @Timeout(value = 5)
    public void payOutAsyncSuccess() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        PayOutRequest request = preparePayOutRequest();

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.payOutAsync(request,
                response -> {
                    assertNotNull(response);
                    assertEquals("0", response.getResponseCode());
                    assertEquals("Accept the service request successfully.", response.getResponseDescription());
                    latch.countDown(); // Signal that the callback has been executed
                },
                error -> fail("Error callback should not be invoked"));

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void payOutAsyncFail() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        PayOutRequest request = preparePayOutRequest();

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.payOutAsync(request,
                response -> fail("Success callback should not be invoked"),
                error -> {
                    assertNotNull(error);
                    assertEquals("Bad Request - Invalid CommandID", error.getMessage());
                    latch.countDown(); // Signal that the error callback has been executed
                });

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Error callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void transactionStatusAsyncSuccess() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.JSON.getValue()).setBody(SUCCESS_RESPONSE));
        TransactionStatusRequest request = prepareTransactionStatusRequest("VALID_ID");

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.transactionStatusAsync(request,
                response -> {
                    assertNotNull(response);
                    assertEquals("0", response.getResponseCode());
                    assertEquals("Accept the service request successfully.", response.getResponseDescription());
                    latch.countDown(); // Signal that the callback has been executed
                },
                error -> fail("Error callback should not be invoked"));

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void transactionStatusAsyncFail() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        TransactionStatusRequest request = prepareTransactionStatusRequest("INVALID_ID");

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.transactionStatusAsync(request,
                response -> fail("Success callback should not be invoked"),
                error -> {
                    assertNotNull(error);
                    assertEquals("Bad Request - Invalid CommandID", error.getMessage());
                    latch.countDown(); // Signal that the error callback has been executed
                });

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Error callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void transactionReversalAsyncSuccess() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        String randomString = String.format("%05d", new Random().nextInt(10000));

        TransactionReversalRequest request = prepareTransactionReversalRequest(randomString);

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.transactionReversalAsync(request,
                response -> {
                    assertNotNull(response);
                    assertEquals("0", response.getResponseCode());
                    assertEquals("Accept the service request successfully.", response.getResponseDescription());
                    latch.countDown(); // Signal that the callback has been executed
                },
                error -> fail("Error callback should not be invoked"));

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void transactionReversalAsyncFail() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        TransactionReversalRequest request = prepareTransactionReversalRequest("1234");
        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.transactionReversalAsync(request,
                response -> fail("Success callback should not be invoked"),
                error -> {
                    assertNotNull(error);
                    assertEquals("Bad Request - Invalid CommandID", error.getMessage());
                    latch.countDown(); // Signal that the error callback has been executed
                });

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Error callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void accountBalanceAsyncSuccess() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SUCCESS_RESPONSE));
        AccountBalanceRequest request = prepareAccountBalanceRequest();

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.accountBalanceAsync(request,
                response -> {
                    assertNotNull(response);
                    assertEquals("0", response.getResponseCode());
                    assertEquals("Accept the service request successfully.", response.getResponseDescription());
                    latch.countDown(); // Signal that the callback has been executed
                },
                error -> fail("Error callback should not be invoked"));

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    @Timeout(value = 5)
    public void accountBalanceAsyncFail() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(FAILURE_RESPONSE));
        AccountBalanceRequest request = prepareAccountBalanceRequest();

        // Create a latch to wait for the callback execution
        CountDownLatch latch = new CountDownLatch(1);

        mpesa.accountBalanceAsync(request,
                response -> fail("Success callback should not be invoked"),
                error -> {
                    assertNotNull(error);
                    assertEquals("Bad Request - Invalid CommandID", error.getMessage());
                    latch.countDown(); // Signal that the error callback has been executed
                });

        // Wait for the callback to execute
        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Error callback should be executed");
    }
    // TEST DATA

    private AccountBalanceRequest prepareAccountBalanceRequest() {
        String randomString = String.format("%04d", new Random().nextInt(10000));
        return AccountBalanceRequest.builder()
                .originatorConversationID(randomString)
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandID("AccountBalance")
                .partyA("600000")
                .identifierType("4")
                .remarks("Testing success")
                .queueTimeOutURL("https://example.com/balance/timeout")
                .resultURL("https://example.com/balance/result")
                .build();
    }

    private TransactionStatusRequest prepareTransactionStatusRequest(String transactionId) {
        return TransactionStatusRequest.builder()
                .originalConversationId("1234")
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandId("TransactionStatusQuery")
                .transactionId(transactionId)
                .partyA("600000")
                .identifierType("4")
                .resultUrl("https://example.com/status/result")
                .queueTimeoutUrl("https://example.com/status/timeout")
                .remarks("Testing")
                .build();
    }

    private PayOutRequest preparePayOutRequest() {
        return PayOutRequest.builder()
                .originatorConversationID("1234")
                .initiatorName("testapiuser")
                .securityCredential("1234567")
                .occassion("StallOwner")
                .commandID("BusinessPayment")
                .partyA("600000")
                .partyB("251711959143")
                .remarks("Test B2C")
                .amount(100)
                .queueTimeOutURL("https://example.com/timeout")
                .resultURL("https://example.com/result")
                .build();
    }

    private TransactionReversalRequest prepareTransactionReversalRequest(String randomString) {
        return TransactionReversalRequest.builder()
                .originatorConversationId(randomString)
                .originalConversationId("12343434")
                .initiator("testapiuser")
                .securityCredential("pMh+yHNYFJL4aKFFWOzY1QDw/GvGBO+4Yy++uT8ezcOmt/c/NPX5/GNS/RXfoFw7pVy5tLJFcGNAu2wZ9tkUEfoZbFEAr78vlt9Rm70usiBeB4CM+FJf4TIWgSF1kCKO7+xGnv1Bma+UbTiUDd8esjYemIUbFPO+w3xuH5Tc8whAiqEsqS+Rs0ZrslJFh7+NA732HkP54nkT918bJnXbCJNM0zZX2xoDM6qb+37vfgt7QIXVxNtRpi9lQFRFxRTZHKA9jZ420tKHpo2HMFzGMvBGaWj4MVSbRfNXWE4ekMkKBsnqFb5wQ+om7O/LQX0zLXPj1nuV/qMh2Cd+mY/knA==")
                .commandId("TransactionReversal")
                .transactionId("QF72BSMQ8W")
                .amount("1050")
                .receiverParty("600000")
                .receiverIdentifierType("11")
                .resultUrl("https://www.myservice:8080/reversal/result")
                .queueTimeOutUrl("https://www.myservice:8080/reversal/timeout")
                .remarks("Testing success")
                .build();
    }
}
