package et.safaricom.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import et.safaricom.Mpesa;
import et.safaricom.config.Configuration;
import et.safaricom.exceptions.DataValidationException;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.payment.request.RegisterUrlRequest;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.response.UssdPushResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.*;

import static et.safaricom.payment.MockData.PAYMENT_VALIDATION_REQUEST;
import static et.safaricom.payment.MockData.USSD_PUSH_REQUEST;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    private Mpesa mpesa;

    @BeforeEach
    void setUp() {
        var config = new Configuration();
        config.setLogLevel(Level.ALL);
        mpesa = new Mpesa(System.getenv("CONSUMER_KEY"), System.getenv("CONSUMER_SECRET"), config);
    }

    @Test
    void triggerUssdPush_successfulResponse() throws MpesaApiException, DataValidationException {
        // Act
        UssdPushResponse response = mpesa.triggerUssdPush(USSD_PUSH_REQUEST);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getResponseCode());
//        assertEquals("Success. Request accepted for processing", response.getResponseDescription());
    }

    @Test
    void triggerUssdPush_withInvalidPhone() throws IOException {
        // Arrange
        // Clone
        ObjectMapper objectMapper = new ObjectMapper();
        UssdPushRequest ussdPushRequest = objectMapper.readValue(objectMapper.writeValueAsString(USSD_PUSH_REQUEST), UssdPushRequest.class);
        ussdPushRequest.setPhoneNumber("0212345678");

        // Act & Assert
        DataValidationException exception = assertThrows(
                DataValidationException.class, () -> mpesa.triggerUssdPush(ussdPushRequest));

        assertEquals("Validation failed:\n" +
                "phoneNumber: Invalid phone number. It must start with '7', '07', or '2517'.\n", exception.getMessage());
    }

    @Test
    void registerPaymentNotificationUrl_withInvalidApiKey() {
        // Arrange
        RegisterUrlRequest request = RegisterUrlRequest.builder().shortCode("174379").responseType("Completed").commandID("RegisterURL").confirmationURL("https://example.com/confirm").validationURL("https://example.com/validate").build();

        // Act & Assert
        MpesaApiException exception = assertThrows(MpesaApiException.class, () -> mpesa.registerPaymentNotificationUrl(request, "sample api key"));

        assertEquals("Unauthorised-Invalid Api Key", exception.getMessage());
    }

    @Test
    void validatePayment() throws DataValidationException {
        // Act
        var response = mpesa.validatePayment(PAYMENT_VALIDATION_REQUEST);

        // Assert
        assertNotNull(response);
        assertEquals(response.getResultCode(), "0");
        assertNotNull(response.getResultDesc(), "Success");
    }

    @Test
    void confirmPayment() throws DataValidationException {
        // Act
        var response = mpesa.confirmPayment(PAYMENT_VALIDATION_REQUEST);

        // Assert
        assertNotNull(response);
        assertEquals("0", response.getResultCode());
        assertNotNull(response.getResultDesc(), "Success");
    }

}