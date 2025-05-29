package et.safaricom.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import et.safaricom.Mpesa;
import et.safaricom.config.Configuration;
import et.safaricom.exceptions.DataValidationException;
import et.safaricom.constants.MediaType;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.response.RegisterUrlResponse;
import et.safaricom.payment.response.SimulateResponse;
import et.safaricom.payment.response.UssdPushResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static et.safaricom.payment.MockData.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("mocked")
class PaymentMockedTest {

    private MockWebServer mockWebServer = new MockWebServer();
    private Mpesa mpesa;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        configureMpesa();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void triggerUssdPush_successfulResponse() throws MpesaApiException, DataValidationException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(TRIGGER_PUSH_SUCCESS_RESPONSE));
        UssdPushResponse response = mpesa.triggerUssdPush(USSD_PUSH_REQUEST);
        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertEquals("Success. Request accepted for processing", response.getResponseDescription());
    }

    @Test
    void triggerUssdPushAsync_successfulResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.JSON.getValue()).setBody(TRIGGER_PUSH_SUCCESS_RESPONSE));
        CountDownLatch latch = new CountDownLatch(1);
        mpesa.triggerUssdPushAsync(USSD_PUSH_REQUEST, response -> {
            assertNotNull(response);
            assertEquals("0", response.getResponseCode());
            assertEquals("Success. Request accepted for processing", response.getResponseDescription());
            latch.countDown();
        }, exception -> fail("Shouldn't throw error"));

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    void triggerUssdPush_errorResponse() throws MpesaApiException, DataValidationException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(TRIGGER_PUSH_ERROR_RESPONSE));

        UssdPushResponse response = mpesa.triggerUssdPush(USSD_PUSH_REQUEST);
        assertNotEquals("0", response.getResponseCode());
    }

    @Test
    void triggerUssdPush_invalidPhoneNumber() throws JsonProcessingException {
        // Clone
        ObjectMapper objectMapper = new ObjectMapper();
        UssdPushRequest ussdPushRequest = objectMapper.readValue(objectMapper.writeValueAsString(USSD_PUSH_REQUEST), UssdPushRequest.class);

        ussdPushRequest.setPhoneNumber("0212345678");
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(INVALID_PHONE_RESPONSE));

        DataValidationException exception = assertThrows(
                DataValidationException.class, () -> mpesa.triggerUssdPush(ussdPushRequest));
        assertEquals("Validation failed:\n" +
                "phoneNumber: Invalid phone number. It must start with '7', '07', or '2517'.\n", exception.getMessage());
    }

    @Test
    void registerPaymentNotificationUrl_withInvalidApiKey() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401).setBody(UNAUTHORISED_API_KEY_RESPONSE));

        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesa.registerPaymentNotificationUrl(REGISTER_URL_REQUEST, "invalid_api_key"));
        assertEquals("Unauthorised-Invalid Api Key", exception.getMessage());
    }

    @Test
    void registerPaymentNotificationUrl_successResponse() throws MpesaApiException, DataValidationException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(REGISTER_URL_SUCCESS_RESPONSE));

        RegisterUrlResponse response = mpesa.registerPaymentNotificationUrl(REGISTER_URL_REQUEST, "api-key--");
        assertEquals("Request processed successfully", response.getHeader().getResponseMessage());
    }

    @Test
    void registerPaymentNotificationUrlAsync_successResponse() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.JSON.getValue()).setBody(REGISTER_URL_SUCCESS_RESPONSE));

        mpesa.registerPaymentNotificationUrlAsync(REGISTER_URL_REQUEST, "api-key--", response -> {
            assertEquals("Request processed successfully", response.getHeader().getResponseMessage());
            latch.countDown();
        }, exception -> fail("Shouldn't throw error"));

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    void registerPaymentNotificationUrl_alreadyRegistered() throws MpesaApiException, DataValidationException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(REGISTER_URL_ERROR_RESPONSE));

        RegisterUrlResponse response = mpesa.registerPaymentNotificationUrl(REGISTER_URL_REQUEST, "api-key--");
        assertEquals("Short Code already Registered", response.getHeader().getResponseMessage());
    }

    @Test
    void registerPaymentNotificationUrlAsync_alreadyRegistered() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.JSON.getValue()).setBody(REGISTER_URL_ERROR_RESPONSE));
        CountDownLatch latch = new CountDownLatch(1);
        mpesa.registerPaymentNotificationUrlAsync(REGISTER_URL_REQUEST, "api-key--",
                response -> {
                    assertEquals("Short Code already Registered", response.getHeader().getResponseMessage());
                    latch.countDown();
                },
                exception -> fail("Shouldn't throw error")
        );

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    void validatePayment_successResponse() throws DataValidationException {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(VALIDATE_SUCCESS_RESPONSE));

        // Act
        var response = mpesa.validatePayment(PAYMENT_VALIDATION_REQUEST);

        // Assert
        assertNotNull(response);
        assertEquals(response.getResultCode(), "0");
        assertNotNull(response.getResultDesc(), "Success");
    }

    @Test
    void validatePaymentAsync_successResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.XML.getValue()).setBody(VALIDATE_SUCCESS_RESPONSE));
        CountDownLatch latch = new CountDownLatch(1);
        mpesa.validatePaymentAsync(PAYMENT_VALIDATION_REQUEST,
                response -> {
                    assertNotNull(response);
                    assertEquals(response.getResultCode(), "0");
                    assertNotNull(response.getResultDesc(), "Success");
                    latch.countDown();
                },
                exception -> fail("Shouldn't throw error")
        );

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    void confirmPayment() throws DataValidationException {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(VALIDATE_SUCCESS_RESPONSE));

        // Act
        var response = mpesa.confirmPayment(PAYMENT_VALIDATION_REQUEST);

        // Assert
        assertNotNull(response);
        assertEquals("0", response.getResultCode());
        assertNotNull(response.getResultDesc(), "Success");
    }

    @Test
    void confirmPaymentAsync_successResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", MediaType.XML.getValue()).setBody(VALIDATE_SUCCESS_RESPONSE));
        CountDownLatch latch = new CountDownLatch(1);
        mpesa.confirmPaymentAsync(PAYMENT_VALIDATION_REQUEST,
                response -> {
                    assertNotNull(response);
                    assertEquals(response.getResultCode(), "0");
                    assertNotNull(response.getResultDesc(), "Success");
                    latch.countDown();
                },
                exception -> fail("Shouldn't throw error")
        );

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    @Test
    void simulatePayment() throws DataValidationException, IOException, MpesaApiException {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SIMULATE_RESPONSE));

        // Act
        SimulateResponse response = mpesa.simulatePayment(SIMULATE_REQUEST);

        // Assert
        assertNotNull(response);
        assertEquals("0", response.getResponseCode());
        assertNotNull(response.getResponseDescription(), "Accept the service request successfully.");
    }

    @Test
    void simulatePaymentAsync_successResponse() throws InterruptedException, DataValidationException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(SIMULATE_RESPONSE));
        CountDownLatch latch = new CountDownLatch(1);
        mpesa.simulatePaymentAsync(SIMULATE_REQUEST,
                response -> {
                    assertNotNull(response);
                    assertEquals("0", response.getResponseCode());
                    assertNotNull(response.getResponseDescription(), "Accept the service request successfully.");
                    latch.countDown();
                },
                exception -> fail("Shouldn't throw error")
        );

        boolean callbackExecuted = latch.await(5, TimeUnit.SECONDS);
        assertTrue(callbackExecuted, "Success callback should be executed");
    }

    private void configureMpesa() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(ACCESS_TOKEN_BODY));
        String baseUrl = mockWebServer.url("/").toString();

        Configuration config = Configuration.builder()
                .logLevel(Level.ALL)
                .baseUrl(baseUrl)
                .build();
        mpesa = new Mpesa("test_consumer_key", "test_consumer_secret", config);
    }
}
