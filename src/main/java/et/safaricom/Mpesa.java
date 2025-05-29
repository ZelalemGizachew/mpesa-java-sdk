package et.safaricom;

import et.safaricom.auth.Authorization;
import et.safaricom.client.Client;
import et.safaricom.client.ClientImpl;
import et.safaricom.commons.response.CallbackError;
import et.safaricom.commons.response.CallbackResponse;
import et.safaricom.commons.response.SuccessResponse;
import et.safaricom.config.Configuration;
import et.safaricom.exceptions.DataValidationException;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.payment.Payment;
import et.safaricom.payment.request.RegisterUrlRequest;
import et.safaricom.payment.request.SimulateRequest;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.request.validation.C2BPaymentValidationRequest;
import et.safaricom.payment.request.validation.ValidationConfirmationRequest;
import et.safaricom.payment.response.RegisterUrlResponse;
import et.safaricom.payment.response.SimulateResponse;
import et.safaricom.payment.response.UssdPushResponse;
import et.safaricom.payment.response.validation.ValidationConfirmationResponse;
import et.safaricom.transaction.Transaction;
import et.safaricom.transaction.request.AccountBalanceRequest;
import et.safaricom.transaction.request.PayOutRequest;
import et.safaricom.transaction.request.TransactionReversalRequest;
import et.safaricom.transaction.request.TransactionStatusRequest;
import et.safaricom.utils.DataValidation;
import et.safaricom.utils.LoggerUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for interacting with the M-Pesa API.
 */
public class Mpesa {
    /**
     * The consumer key provided by Safaricom.
     */
    @Getter
    @Setter
    private String consumerKey;

    /**
     * The consumer secret provided by Safaricom.
     */
    @Getter
    @Setter
    private String consumerSecret;

    /**
     * The authorization object that provides the token to be used in the requests.
     */
    @Getter
    private final Authorization authorization;

    /**
     * The configuration object that provides the configuration for the API.
     */
    @Getter
    @Setter
    private Configuration configuration;

    /**
     * The client object that provides the client to be used in the requests.
     */
    @Getter
    @Setter
    private Client client;

    /**
     * The transaction object that provides the transaction to be used in the
     * requests.
     */
    private final Transaction transaction;

    /**
     * Represents a payment process or transaction handled within the Mpesa class.
     */
    private final Payment payment;

    private final Validator validator;

    /**
     * Constructor that takes the consumer key and secret as parameters.
     *
     * @param consumerKey    the consumer key provided by Safaricom.
     * @param consumerSecret the consumer secret provided by Safaricom.
     */
    public Mpesa(String consumerKey, String consumerSecret) {
        this(consumerKey, consumerSecret, new Configuration());
    }

    /**
     * Constructor that takes the consumer key, consumer secret, and configuration
     * as parameters.
     *
     * @param consumerKey    the consumer key provided by Safaricom.
     * @param consumerSecret the consumer secret provided by Safaricom.
     * @param configuration  the configuration object that provides the
     *                       configuration for the API.
     */
    public Mpesa(String consumerKey, String consumerSecret, Configuration configuration) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.configuration = configuration;

        try (ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())  // Disable EL interpolation
                .buildValidatorFactory()) {
            this.validator = factory.getValidator();
        }
        Logger logger = LoggerUtils.createLogger(Mpesa.class, configuration.getLogLevel());
        this.client = new ClientImpl(this.configuration, logger);
        this.authorization = new Authorization(consumerKey, consumerSecret, this.client, logger);
        this.transaction = new Transaction(this.authorization, this.client, logger);
        this.payment = new Payment(this.authorization, this.client, logger);
    }

    /**
     * Sends a synchronous request to the M-Pesa API to perform a business to
     * customer (B2C) payment.
     *
     * @param payOutRequest the {@link PayOutRequest} containing the payment
     *                      details.
     * @return the {@link SuccessResponse} with the result of the payment.
     * @throws MpesaApiException       if any exception occurs during request processing.
     * @throws DataValidationException if the input data fails validation.
     * @throws IOException             if an input or output exception occurs during the
     */
    public SuccessResponse payOut(PayOutRequest payOutRequest) throws IOException, MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, payOutRequest);

        return transaction.payOutSync(payOutRequest);
    }

    /**
     * Sends a synchronous request to the M-Pesa API to query the status of a
     * transaction.
     *
     * @param transactionStatusRequest the {@link TransactionStatusRequest}
     *                                 containing the transaction details.
     * @return the {@link SuccessResponse} with the result of the
     * transaction status query.
     * @throws MpesaApiException       if any exception occurs during request processing.
     * @throws DataValidationException if the input data fails validation.
     * @throws IOException             if an input or output exception occurs during the
     */
    public SuccessResponse transactionStatus(TransactionStatusRequest transactionStatusRequest)
            throws IOException, MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, transactionStatusRequest);

        return transaction.transactionStatusSync(transactionStatusRequest);
    }

    /**
     * Sends a synchronous request to the M-Pesa API to reverse a transaction.
     *
     * @param transactionReversalRequest the {@link TransactionReversalRequest}
     *                                   containing the transaction reversal
     *                                   details.
     * @return the {@link SuccessResponse} with the result of the transaction
     * reversal.
     * @throws MpesaApiException       if any exception occurs during request processing.
     * @throws DataValidationException if the input data fails validation.
     * @throws IOException             if an input or output exception occurs during the
     */
    public SuccessResponse transactionReversal(TransactionReversalRequest transactionReversalRequest)
            throws IOException, MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, transactionReversalRequest);

        return transaction.transactionReversalSync(transactionReversalRequest);
    }

    /**
     * Sends a synchronous request to the M-Pesa API to query the balance of the
     * short code.
     *
     * @param accountBalanceRequest the {@link AccountBalanceRequest} containing the
     *                              transaction details.
     * @return the {@link SuccessResponse} with the result of the account balance
     * query.
     * @throws MpesaApiException       if any exception occurs during request processing.
     * @throws DataValidationException if the input data fails validation.
     * @throws IOException             if an input or output exception occurs during the
     */
    public SuccessResponse accountBalance(AccountBalanceRequest accountBalanceRequest)
            throws IOException, MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, accountBalanceRequest);

        return transaction.accountBalanceSync(accountBalanceRequest);
    }

    /**
     * Initiates a customer-to-business (C2B) payment by sending a USSD Push request
     * to the M-PESA API, prompting the customer to complete the transaction by
     * entering
     * their M-PESA PIN.
     *
     * @param request the {@link UssdPushRequest} containing payment details like
     *                amount, phone number, and callback URL.
     * @return a {@link Response} object with the transaction status and any error
     * messages.
     * @throws MpesaApiException       if there is an error processing the API request,
     *                                 such as invalid parameters,
     *                                 authentication issues, or server-side failures.
     * @throws DataValidationException if the input data fails validation.
     */
    public UssdPushResponse triggerUssdPush(UssdPushRequest request) throws MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, request);

        return payment.triggerUssdPush(request);
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
        DataValidation.validateInput(validator, request, callbackError);
        payment.triggerUssdPushAsync(request, callbackResponse, callbackError);
    }

    /**
     * Registers validation and confirmation URLs with M-PESA for payment
     * notifications.
     * Sends a request to the M-PESA Register URL API and returns the response.
     *
     * @param request the {@link RegisterUrlRequest} containing shortcode, URLs,
     *                etc.
     * @param apiKey  the API key required for authentication.
     * @return the {@link RegisterUrlResponse} with the API response details.
     * @throws MpesaApiException       if there is an error processing the API request,
     *                                 such as invalid parameters,
     *                                 authentication issues, or server-side failures.
     * @throws DataValidationException if the input data fails validation.
     */
    public RegisterUrlResponse registerPaymentNotificationUrl(RegisterUrlRequest request, String apiKey)
            throws MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, request);

        return payment.registerPaymentNotificationUrl(request, apiKey);
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
        DataValidation.validateInput(validator, request, callbackError);
        payment.registerPaymentNotificationUrlAsync(request, apiKey, callbackResponse, callbackError);
    }

    /**
     * Validates an incoming payment request by checking transaction details against
     * predefined criteria.
     * Ensures that payments are legitimate before processing.
     *
     * @param request the {@link C2BPaymentValidationRequest} containing transaction
     *                details
     *                like amount, MSISDN, and business shortcode.
     * @return a {@link ValidationConfirmationResponse} with the result of the
     * validation.
     * @throws DataValidationException if the input data fails validation.
     */
    public ValidationConfirmationResponse validatePayment(C2BPaymentValidationRequest request) throws DataValidationException {
        DataValidation.validateInput(validator, request);

        return payment.validatePayment(request);
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
        DataValidation.validateInput(validator, request, callbackError);
        payment.validatePaymentAsync(request, callbackResponse, callbackError);
    }

    /**
     * Confirms a processed payment after validation, notifying the business of the
     * transaction completion. Triggers a confirmation notification once the payment
     * is processed.
     *
     * @param request the {@link C2BPaymentValidationRequest} containing transaction
     *                details
     *                like amount, MSISDN, and confirmation status.
     * @return a {@link ValidationConfirmationResponse} with the confirmation
     * result.
     * @throws DataValidationException if the input data fails validation.
     */
    public ValidationConfirmationResponse confirmPayment(C2BPaymentValidationRequest request) throws DataValidationException {
        DataValidation.validateInput(validator, request);

        return payment.confirmPayment(request);
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
        DataValidation.validateInput(validator, request, callbackError);
        payment.confirmPaymentAsync(request, callbackResponse, callbackError);
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
     * @throws IOException       if an I/O error occurs
     * @throws MpesaApiException if an error occurs in the M-PESA API
     */
    public SimulateResponse simulatePayment(SimulateRequest request) throws IOException, MpesaApiException, DataValidationException {
        DataValidation.validateInput(validator, request);
        return payment.simulatePayment(request);
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
    public void simulatePaymentAsync(SimulateRequest request, CallbackResponse<SimulateResponse> callbackResponse, CallbackError callbackError) throws DataValidationException {
        DataValidation.validateInput(validator, request);
        payment.simulatePaymentAsync(request, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the M-Pesa API to perform a business to
     * customer (B2C) payment.
     *
     * @param payOutRequest    the {@link PayOutRequest} containing the payment
     *                         details.
     * @param callbackResponse the callback to be executed on successful completion
     *                         of the request.
     * @param callbackError    the callback to be executed if an error occurs
     *                         during request processing.
     */
    public void payOutAsync(PayOutRequest payOutRequest, CallbackResponse<SuccessResponse> callbackResponse,
                            CallbackError callbackError) {
        DataValidation.validateInput(validator, payOutRequest, callbackError);
        transaction.payOutAsync(payOutRequest, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the M-Pesa API to query the status of a
     * transaction.
     *
     * @param transactionStatusRequest the {@link TransactionStatusRequest}
     *                                 containing the transaction details.
     * @param callbackResponse         the callback to be executed on successful
     *                                 completion of the request.
     * @param callbackError            the callback to be executed if an error
     *                                 occurs during request processing.
     */
    public void transactionStatusAsync(TransactionStatusRequest transactionStatusRequest,
                                       CallbackResponse<SuccessResponse> callbackResponse, CallbackError callbackError) {
        DataValidation.validateInput(validator, transactionStatusRequest, callbackError);
        transaction.transactionStatusAsync(transactionStatusRequest, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the M-Pesa API to reverse a transaction.
     *
     * @param transactionReversalRequest the {@link TransactionReversalRequest}
     *                                   containing the transaction reversal
     *                                   details.
     * @param callbackResponse           the callback to be executed on successful
     *                                   completion of the request.
     * @param callbackError              the callback to be executed if an error
     *                                   occurs during request processing.
     */
    public void transactionReversalAsync(TransactionReversalRequest transactionReversalRequest,
                                         CallbackResponse<SuccessResponse> callbackResponse, CallbackError callbackError) {
        DataValidation.validateInput(validator, transactionReversalRequest, callbackError);
        transaction.transactionReversalAsync(transactionReversalRequest, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the M-Pesa API to query the balance of the
     * short code.
     *
     * @param accountBalanceRequest the {@link AccountBalanceRequest} containing the
     *                              transaction details.
     * @param callbackResponse      the callback to be executed on successful
     *                              completion of the request.
     * @param callbackError         the callback to be executed if an error occurs
     *                              during request processing.
     */
    public void accountBalanceAsync(AccountBalanceRequest accountBalanceRequest,
                                    CallbackResponse<SuccessResponse> callbackResponse, CallbackError callbackError) {
        DataValidation.validateInput(validator, accountBalanceRequest, callbackError);
        transaction.accountBalanceAsync(accountBalanceRequest, callbackResponse, callbackError);
    }
}
