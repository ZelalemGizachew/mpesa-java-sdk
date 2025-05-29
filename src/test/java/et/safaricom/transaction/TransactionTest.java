package et.safaricom.transaction;

import et.safaricom.Mpesa;
import et.safaricom.commons.response.SuccessResponse;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.transaction.request.AccountBalanceRequest;
import et.safaricom.transaction.request.PayOutRequest;
import et.safaricom.transaction.request.TransactionReversalRequest;
import et.safaricom.transaction.request.TransactionStatusRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    private Mpesa mpesa;

    @BeforeEach
    public void setUp() {
        String consumerKey = System.getenv("CONSUMER_KEY");
        String consumerSecret = System.getenv("CONSUMER_SECRET");

        if (consumerKey == null || consumerSecret == null) {
            throw new IllegalStateException("Environment variables CONSUMER_KEY and CONSUMER_SECRET must be set");
        }
        mpesa = new Mpesa(consumerKey, consumerSecret);
    }

    @Test
    public void payOutSyncFail() {
        PayOutRequest request = PayOutRequest.builder()
                .originatorConversationID("1234")
                .initiatorName("testapiuser")
                .securityCredential("1234567")
                .occassion("StallOwner")
                .commandID("BusinessPayment")
                .partyA("600000")
                .partyB("251711959143")
                .remarks("Test B2C")
                .amount(100)
                .queueTimeOutURL("https://www.myservice:8080/b2c/result")
                .resultURL("https://www.myservice:8080/b2c/result")
                .build();

        assertThrows(MpesaApiException.class, () -> mpesa.payOut(request));
    }

    @Test
    public void payOutSyncSuccess() throws Exception {
        String randomString = String.format("%04d", new Random().nextInt(10000));
        PayOutRequest request = PayOutRequest.builder()
                .originatorConversationID(randomString)
                .initiatorName("testapiuser")
                .securityCredential("1234567")
                .occassion("StallOwner")
                .commandID("BusinessPayment")
                .partyA("600000")
                .partyB("251711959143")
                .remarks("Test B2C")
                .amount(100)
                .queueTimeOutURL("https://www.myservice:8080/b2c/result")
                .resultURL("https://www.myservice:8080/b2c/result")
                .build();

        SuccessResponse successResponse = mpesa.payOut(request);

        assertNotNull(successResponse.getResponseCode());
    }

    @Test
    public void transactionStatusSyncFail() {
        TransactionStatusRequest request =  TransactionStatusRequest.builder()
                .originalConversationId("1234")
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandId("TransactionStatusQueryUI")
                .transactionId("INVALID_ID")
                .partyA("600000")
                .identifierType("4")
                .resultUrl("https://www.myservice:8080/status/result")
                .queueTimeoutUrl("https://www.myservice:8080/status/timeout")
                .remarks("Testing failure")
                .build();

        assertThrows(MpesaApiException.class, () -> mpesa.transactionStatus(request));
    }

    @Test
    public void transactionStatusSyncSuccess() throws Exception {
        TransactionStatusRequest request =  TransactionStatusRequest.builder()
                .originalConversationId("1234")
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandId("TransactionStatusQuery")
                .transactionId("VALID_ID")
                .partyA("600000")
                .identifierType("4")
                .resultUrl("https://www.myservice:8080/status/result")
                .queueTimeoutUrl("https://www.myservice:8080/status/timeout")
                .remarks("Testing success")
                .build();

        SuccessResponse successResponse = mpesa.transactionStatus(request);

        assertNotNull(successResponse.getResponseCode());
    }

    @Test
    public void transactionReversalSyncFail() {
        TransactionReversalRequest request = TransactionReversalRequest.builder()
                .originatorConversationId("1234")
                .originalConversationId("1234567")
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandId("TransactionReversal")
                .transactionId("INVALID_ID")
                .amount("100")
                .receiverParty("600000")
                .receiverIdentifierType("11")
                .resultUrl("https://www.myservice:8080/reversal/result")
                .queueTimeOutUrl("https://www.myservice:8080/reversal/timeout")
                .remarks("Testing failure")
                .build();

        assertThrows(MpesaApiException.class, () -> mpesa.transactionReversal(request));
    }

    @Test
    public void transactionReversalSyncSuccess() throws Exception {
        String randomString = String.format("%04d", new Random().nextInt(10000));

        TransactionReversalRequest request =  TransactionReversalRequest.builder()
                .originatorConversationId(randomString)
                .originalConversationId("1234567")
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

        SuccessResponse successResponse = mpesa.transactionReversal(request);

        assertNotNull(successResponse.getResponseCode());
    }

    @Test
    public void accountBalanceSyncFail() {
        AccountBalanceRequest request = AccountBalanceRequest.builder()
                .originatorConversationID("1234")
                .initiator("testapiuser")
                .securityCredential("1234567")
                .commandID("AccountBalance")
                .partyA("600000")
                .identifierType("4")
                .remarks("Testing success")
                .queueTimeOutURL("https://www.myservice:8080/balance/timeout")
                .resultURL("https://www.myservice:8080/balance/result")
                .build();

        assertThrows(MpesaApiException.class, () -> mpesa.accountBalance(request));
    }

    @Test
    public void accountBalanceSyncSuccess() throws Exception {
        String randomString = String.format("%014d", new Random().nextInt(1000000));

        AccountBalanceRequest request =  AccountBalanceRequest.builder()
        .originatorConversationID(randomString)
        .initiator("testapiuser")
        .securityCredential("1234567")
        .commandID("AccountBalance")
        .partyA("600000")
        .identifierType("4")
        .remarks("Testing success")
        .queueTimeOutURL("https://www.myservice:8080/balance/timeout")
        .resultURL("https://www.myservice:8080/balance/result")
                .build();

        SuccessResponse successResponse = mpesa.accountBalance(request);
        assertNotNull(successResponse.getResponseCode());
    }
}
