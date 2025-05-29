package et.safaricom.payment;

import et.safaricom.payment.request.ReferenceData;
import et.safaricom.payment.request.RegisterUrlRequest;
import et.safaricom.payment.request.SimulateRequest;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.request.validation.C2BPaymentValidationRequest;
import et.safaricom.payment.request.validation.KYCInfo;

import java.util.Arrays;
import java.util.List;

class MockData {
    public static final UssdPushRequest USSD_PUSH_REQUEST = UssdPushRequest.builder()
            .merchantRequestId("4354354351")
            .businessShortCode("174379")
            .passkey("bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919")
            .transactionType("CustomerPayBillOnline")
            .amount("10000")
            .partyA("251703434349")
            .partyB("600000")
            .phoneNumber("251708374149")
            .transactionDesc("Monthly Unlimited Package via Chatbot")
            .callBackUrl("https://example.com/callback")
            .accountReference("34q5125")
            .referenceData(Arrays.asList(
                    new ReferenceData("BundleName", "Monthly Unlimited Bundle"),
                    new ReferenceData("BundleType", "Self"),
                    new ReferenceData("TINNumber", "89234093223"))
            ).build();
    public static final String ACCESS_TOKEN_BODY = "{\n" +
            "   \"access_token\": \"05RpFfThkohCr4K1FAtSjXNDAz1a\",\n" +
            "   \"token_type\": \"Bearer\",\n" +
            "   \"expires_in\" : \"3599\"\n" +
            "}\n";
    public static final String TRIGGER_PUSH_ERROR_RESPONSE = "{\n" +
            "    \"MerchantRequestID\": \"SFC-Testing-9146-4216-9455-e3947ac570fc\",\n" +
            "    \"CheckoutRequestID\": \"ws_CO_1909202411150986035007\",\n" +
            "    \"ResponseCode\": \"SVC0403\",\n" +
            "    \"ResponseDescription\": \"Forbidden request as the provided Password is incorrect for: uri=/api\",\n" +
            "    \"CustomerMessage\": \"Forbidden request as the provided Password is incorrect\"\n" +
            "}\n";
    public static final String TRIGGER_PUSH_SUCCESS_RESPONSE = "{\n" +
            "    \"MerchantRequestID\": \"9cae-431a-9bb5-0e58fd6aced6\",\n" +
            "    \"CheckoutRequestID\": \"ws_CO_1202202404292020468057\",\n" +
            "    \"ResponseCode\": \"0\",\n" +
            "    \"ResponseDescription\": \"Success. Request accepted for processing\",\n" +
            "    \"CustomerMessage\": \"Success. Request accepted for processing\"\n" +
            "}\n";
    public static final String INVALID_PHONE_RESPONSE = "{\"errorCode\":\"400\",\"errorMessage\":\"Invalid PhoneNumber\"}";
    public static final String UNAUTHORISED_API_KEY_RESPONSE = "{\"header\": {\"requestRefId\": \"1641-49dc-95b9-34880e1789eb303659\",\"responseMessage\": \"Unauthorised-Invalid Api Key\",\"responseCode\": \"401\",\"customerMessage\": \"Unauthorised-Invalid Api Key\",\"timestamp\": \"20250108093712\"},\"body\": {}}";
    public static final String REGISTER_URL_SUCCESS_RESPONSE = "{\"header\": {\"responseCode\": 200,\"responseMessage\": \"Request processed successfully\",\"customerMessage\": \"Request processed successfully\",\"timestamp\": \"2024-02-12T02:20:31.390\"}}";
    public static final String REGISTER_URL_ERROR_RESPONSE = "{\"header\": {\"responseCode\": 400,\"responseMessage\": \"Short Code already Registered\",\"customerMessage\": \"Short Code already Registered\",\"timestamp\": \"2024-02-12T02:20:31.390\"}}";
    public static final String VALIDATE_SUCCESS_RESPONSE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:c2b=\"http://cps.huawei.com/cpsinterface/c2bpayment\">\n" +
            "    <soapenv:Header/>\n" +
            "    <soapenv:Body>\n" +
            "        <c2b:C2BPaymentValidationResult>\n" +
            "            <ResultCode>0</ResultCode>\n" +
            "            <ResultDesc>Success</ResultDesc>\n" +
            "            <ThirdPartyTransID>1234567890</ThirdPartyTransID>\n" +
            "        </c2b:C2BPaymentValidationResult>\n" +
            "    </soapenv:Body>\n" +
            "</soapenv:Envelope>";
    public static final RegisterUrlRequest REGISTER_URL_REQUEST = RegisterUrlRequest.builder()
            .shortCode("174379")
            .responseType("Completed")
            .commandID("RegisterURL")
            .confirmationURL("https://example.com/confirm")
            .validationURL("https://example.com/validate")
            .build();
    public static final C2BPaymentValidationRequest PAYMENT_VALIDATION_REQUEST = C2BPaymentValidationRequest.builder()
            .transType("CustomerPayBillOnline")
            .transID("NXT123456")
            .transTime("20250104135017")
            .transAmount("1000.0")
            .businessShortCode("174379")
            .billRefNumber("Invoice123")
            .orgAccountBalance("5000.0")
            .thirdPartyTransID("TP67890")
            .msisdn("251708374149")
            .firstName("John")
            .middleName("Doe")
            .lastName("Smith")
            .kycInfo(List.of(new KYCInfo("Doe")))
            .invoiceNumber("Invoice123")
            .build();
    public static final SimulateRequest SIMULATE_REQUEST = SimulateRequest.builder()
            .commandID("CustomerPayBillOnline")
            .amount("110")
            .msisdn("251945628580")
            .billRefNumber("091091")
            .shortCode("443443")
            .build();
    public static final String SIMULATE_RESPONSE = "{\n" +
            "    \"ConversationID\": \"AG_20240209_70205ca849aecd7fbd7f\",\n" +
            "    \"OriginatorConversationID\": \"77fd-4542-b4f4-1748deeeb48f\",\n" +
            "    \"ResponseCode\": \"0\",\n" +
            "    \"ResponseDescription\": \"Accept the service request successfully.\"\n" +
            "}\n";

}
