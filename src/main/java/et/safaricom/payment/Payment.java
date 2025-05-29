package et.safaricom.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import et.safaricom.auth.Authorization;
import et.safaricom.client.Client;
import et.safaricom.client.ClientUtils;
import et.safaricom.client.RequestClient;
import et.safaricom.client.ResponseClient;
import et.safaricom.commons.response.CallbackError;
import et.safaricom.commons.response.CallbackResponse;
import et.safaricom.commons.response.MpesaError;
import et.safaricom.constants.Endpoints;
import et.safaricom.constants.MediaType;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.exceptions.MpesaSdkException;
import et.safaricom.payment.request.RegisterUrlRequest;
import et.safaricom.payment.request.SimulateRequest;
import et.safaricom.payment.request.UssdPushApiRequest;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.request.validation.C2BPaymentValidationRequest;
import et.safaricom.payment.request.validation.ConfirmationRequest;
import et.safaricom.payment.request.validation.ValidationConfirmationRequest;
import et.safaricom.payment.response.RegisterUrlResponse;
import et.safaricom.payment.response.SimulateResponse;
import et.safaricom.payment.response.UssdPushResponse;
import et.safaricom.payment.response.validation.ValidationConfirmationResponse;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The {@code Payment} class provides methods for initiating and managing
 * customer-to-business (C2B) payments using the M-Pesa API.
 */
public class Payment {
    private final Authorization authorization;
    private final Client client;
    private final ClientUtils clientUtils;
    private final Logger log;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Payment(Authorization authorization, Client client, Logger log) {
        this.authorization = authorization;
        this.client = client;
        this.log = log;
        this.clientUtils = new ClientUtils(client, authorization, log);
    }

    /**
     * Initiates a customer-to-business (C2B) payment by sending a USSD Push request
     * to the M-PESA API, prompting the customer to complete the transaction by entering
     * their M-PESA PIN.
     *
     * @param request the {@link UssdPushRequest} containing payment details like
     *                amount, phone number, and callback URL.
     * @return a {@link Response} object with the transaction status and any error
     * messages.
     * @throws MpesaApiException if there is an error processing the API request, such as invalid parameters,
     *                           authentication issues, or server-side failures.
     */
    public UssdPushResponse triggerUssdPush(UssdPushRequest request) throws MpesaApiException {
        log.fine("Triggering ussd push with ussd push request: " + request);
        UssdPushApiRequest pushApiRequest = getUssdPushApiRequest(request);

        RequestClient<UssdPushApiRequest> requestClient = RequestClient.<UssdPushApiRequest>builder()
                .method("POST")
                .url(Endpoints.PUSH_CHECKOUT.getValue())
                .authorization(authorization.getAccessToken())
                .body(pushApiRequest)
                .contentType(MediaType.JSON)
                .build();

        try {
            ResponseClient response = client.sendSyncRequest(requestClient);

            String responseBody = response.getBody();

            if (response.getStatusCode() % 400 < 100) {
                MpesaError mpesaError = objectMapper.readValue(responseBody, MpesaError.class);
                log.severe("Encountered client error while executing ussd push: " + mpesaError.getErrorMessage());
                throw new MpesaApiException(mpesaError.getErrorMessage(), mpesaError);
            }

            if (responseBody == null) throw new MpesaSdkException("Body is null");

            UssdPushResponse ussdPushResponse = objectMapper.readValue(responseBody, UssdPushResponse.class);
            log.fine("ussd push response: " + ussdPushResponse);
            return ussdPushResponse;
        } catch (IOException e) {
            log.severe("Encountered error while executing ussd push:" + e.getMessage());
            throw new MpesaSdkException(e.getMessage(), e);
        }
    }

    private static UssdPushApiRequest getUssdPushApiRequest(UssdPushRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String pass = request.getBusinessShortCode() + request.getPasskey() + timestamp;
        String password = Base64.getEncoder().encodeToString(pass.getBytes());
        UssdPushApiRequest pushApiRequest = UssdPushApiRequest.builder()
                .merchantRequestId(request.getMerchantRequestId())
                .businessShortCode(request.getBusinessShortCode())
                .password(password)
                .timestamp(timestamp)
                .transactionType(request.getTransactionType())
                .transactionDesc(request.getTransactionDesc())
                .amount(request.getAmount())
                .partyA(request.getPartyA())
                .partyB(request.getPartyB())
                .phoneNumber(request.getPhoneNumber())
                .callBackUrl(request.getCallBackUrl())
                .accountReference(request.getAccountReference())
                .referenceData(request.getReferenceData())
                .build();
        return pushApiRequest;
    }

    /**
     * Initiates asynchronous a customer-to-business (C2B) payment by sending a USSD Push request
     * to the M-PESA API, prompting the customer to complete the transaction by entering
     * their M-PESA PIN.
     *
     * @param request          the {@link UssdPushRequest} containing payment details like
     *                         amount, phone number, and callback URL.
     * @param callbackResponse the callback to be executed on successful completion
     *                         of the request.
     * @param callbackError    the callback to be executed if an error occurs
     *                         during request processing.
     */
    public void triggerUssdPushAsync(UssdPushRequest request, CallbackResponse<UssdPushResponse> callbackResponse, CallbackError callbackError) {
        log.fine("Triggering ussd push with ussd push request: " + request);
        UssdPushApiRequest ussdPushApiRequest = getUssdPushApiRequest(request);
        this.clientUtils.processASyncRequest(Endpoints.PUSH_CHECKOUT.getValue(), ussdPushApiRequest, UssdPushResponse.class, callbackResponse, callbackError);
    }

    /**
     * Registers validation and confirmation URLs with M-PESA for payment notifications.
     * Sends a request to the M-PESA Register URL API and returns the response.
     *
     * @param request the {@link RegisterUrlRequest} containing shortcode, URLs, etc.
     * @param apiKey  the API key required for authentication.
     * @return the {@link RegisterUrlResponse} with the API response details.
     * @throws MpesaApiException if there is an error processing the API request, such as invalid parameters,
     *                           authentication issues, or server-side failures.
     */
    public RegisterUrlResponse registerPaymentNotificationUrl(RegisterUrlRequest request, String apiKey) throws MpesaApiException {
        log.fine("Registering URL with request: " + request);

        RequestClient<RegisterUrlRequest> requestClient = RequestClient.<RegisterUrlRequest>builder()
                .url(Endpoints.REGISTER_URL.getValue())
                .method("POST")
                .queryParams(Map.of("apiKey", apiKey))
                .authorization(authorization.getAccessToken())
                .body(request)
                .contentType(MediaType.JSON)
                .build();

        try {
            ResponseClient response = client.sendSyncRequest(requestClient);
            String responseBody = response.getBody();

            if (response.getStatusCode() % 400 < 100) {
                RegisterUrlResponse urlResponse = objectMapper.readValue(responseBody, RegisterUrlResponse.class);
                log.severe("Error occured while registering URL: " + urlResponse);
                throw new MpesaApiException(urlResponse.getHeader().getResponseMessage(), MpesaError.builder()
                        .errorCode(urlResponse.getHeader().getResponseCode())
                        .errorMessage(urlResponse.getHeader().getCustomerMessage())
                        .build());
            }

            if (responseBody == null) throw new MpesaSdkException("Body is null");
            ObjectMapper objectMapper = new ObjectMapper();
            RegisterUrlResponse registerUrlResponse = objectMapper.readValue(responseBody, RegisterUrlResponse.class);
            log.fine("Register URL response body: " + registerUrlResponse);
            return registerUrlResponse;
        } catch (IOException e) {
            log.severe("Error occurred during URL registration: " + e.getMessage());
            throw new MpesaSdkException("Error occurred during URL registration: " + e.getMessage(), e);
        }
    }

    /**
     * Registers validation and confirmation URLs with M-PESA for payment notifications asynchronously.
     * Sends a request to the M-PESA Register URL API and returns the response.
     *
     * @param request          the {@link RegisterUrlRequest} containing shortcode, URLs, etc.
     * @param apiKey           the API key required for authentication.
     * @param callbackResponse the callback to be executed on successful completion
     *                         of the request.
     * @param callbackError    the callback to be executed if an error occurs
     *                         during request processing.
     */
    public void registerPaymentNotificationUrlAsync(RegisterUrlRequest request, String apiKey, CallbackResponse<RegisterUrlResponse> callbackResponse, CallbackError callbackError) {
        log.fine("Registering URL with request: " + request);
        this.clientUtils.processASyncRequest(Endpoints.PUSH_CHECKOUT.getValue(), request, Map.of("apikey", apiKey), MediaType.JSON, RegisterUrlResponse.class, callbackResponse, callbackError);
    }

    /**
     * Validates an incoming payment request by checking transaction details against
     * predefined criteria.
     * Ensures that payments are legitimate before processing.
     *
     * @param request the {@link ValidationConfirmationRequest} containing transaction details
     *                like amount, MSISDN, and business shortcode.
     * @return a {@link ValidationConfirmationResponse} with the result of the validation.
     */
    public ValidationConfirmationResponse validatePayment(C2BPaymentValidationRequest request) {
        RequestClient<ValidationConfirmationRequest> requestClient = RequestClient.<ValidationConfirmationRequest>builder()
                .url(Endpoints.VALIDATE.getValue())
                .method("POST")
                .authorization(authorization.getAccessToken())
                .body(new ValidationConfirmationRequest(request))
                .contentType(MediaType.XML)
                .build();

        try {
            return validateConfirmPayment(requestClient);
        } catch (Exception e) {
            throw new MpesaSdkException("Error occurred during URL registration: " + e.getMessage(), e);
        }
    }

    /**
     * Validates an incoming payment request asynchronously by checking transaction details against
     * predefined criteria.
     * Ensures that payments are legitimate before processing.
     *
     * @param request          the {@link ValidationConfirmationRequest} containing transaction details
     * @param callbackResponse the callback to be executed on successful completion
     *                         of the request.
     * @param callbackError    the callback to be executed if an error occurs
     *                         during request processing.
     */
    public void validatePaymentAsync(C2BPaymentValidationRequest request, CallbackResponse<ValidationConfirmationResponse> callbackResponse, CallbackError callbackError) {
        log.fine("Registering URL with request: " + request);
        this.clientUtils.processASyncRequest(Endpoints.VALIDATE.getValue(), request, Map.of(), MediaType.XML, ValidationConfirmationResponse.class, callbackResponse, callbackError);
    }

    /**
     * Confirms a processed payment after validation, notifying the business of the
     * transaction completion. Triggers a confirmation notification once the payment
     * is processed.
     *
     * @param request the {@link C2BPaymentValidationRequest} containing transaction details
     *                like amount, MSISDN, and confirmation status.
     * @return a {@link ValidationConfirmationResponse} with the confirmation result.
     */
    public ValidationConfirmationResponse confirmPayment(C2BPaymentValidationRequest request) {
        RequestClient<ConfirmationRequest> requestClient = RequestClient.<ConfirmationRequest>builder()
                .url(Endpoints.CONFIRM.getValue())
                .method("POST")
                .authorization(authorization.getAccessToken())
                .body(new ConfirmationRequest(request))
                .contentType(MediaType.XML)
                .build();

        try {
            return validateConfirmPayment(requestClient);
        } catch (Exception e) {
            throw new MpesaSdkException("Error occurred during confirmation: " + e.getMessage(), e);
        }
    }

    /**
     * Confirms a processed payment asynchronously after validation, notifying the business of the
     * transaction completion. Triggers a confirmation notification once the payment
     * is processed.
     *
     * @param request the {@link C2BPaymentValidationRequest} containing transaction details
     *                like amount, MSISDN, and confirmation status.
     */
    public void confirmPaymentAsync(C2BPaymentValidationRequest request, CallbackResponse<ValidationConfirmationResponse> callbackResponse, CallbackError callbackError) {
        log.fine("Confirming URL with request: " + request);
        this.clientUtils.processASyncRequest(Endpoints.CONFIRM.getValue(), request, Map.of(), MediaType.XML, ValidationConfirmationResponse.class, callbackResponse, callbackError);
    }

    private <T> ValidationConfirmationResponse validateConfirmPayment(RequestClient<T> requestClient) throws IOException {
        ResponseClient response = client.sendSyncRequest(requestClient);
        if (response.getBody() == null) throw new MpesaSdkException("Body is null");

        String responseBody = response.getBody();
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode rootNode = xmlMapper.readTree(responseBody);

        return xmlMapper.treeToValue(
                rootNode.path("Body").path("C2BPaymentValidationResult"), // Navigate to the inner element (skipping the parent tags)
                ValidationConfirmationResponse.class
        );
    }

    /**
     * Simulates an M-PESA Customer Initiated Payment.
     * <p>
     * Allows testing of customer payment initiation scenarios.
     * Ensure validation and confirmation endpoints are registered via the Register URL API.
     * </p>
     *
     * @param request the payment simulation details
     * @return the simulation result
     * @throws IOException if an I/O error occurs
     * @throws MpesaApiException if an error occurs in the M-PESA API
     */
    public SimulateResponse simulatePayment(SimulateRequest request) throws IOException, MpesaApiException {
        log.fine("Simulating payment with request: " + request);
        return clientUtils.processSyncRequest(Endpoints.SIMULATE.getValue(), request, SimulateResponse.class);
    }

    /**
     * Asynchronously simulates an M-PESA Customer Initiated Payment.
     * <p>
     * Allows testing of customer payment initiation scenarios.
     * Ensure validation and confirmation endpoints are registered via the Register URL API.
     * </p>
     *
     * @param request the payment simulation details
     */
    public void simulatePaymentAsync(SimulateRequest request, CallbackResponse<SimulateResponse> callbackResponse, CallbackError callbackError) {
        log.fine("Simulating payment with request: " + request);
        clientUtils.processASyncRequest(Endpoints.SIMULATE.getValue(), request, SimulateResponse.class, callbackResponse, callbackError);
    }
}
