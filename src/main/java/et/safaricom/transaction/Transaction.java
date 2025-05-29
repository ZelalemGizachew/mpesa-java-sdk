package et.safaricom.transaction;

import et.safaricom.auth.Authorization;
import et.safaricom.client.Client;
import et.safaricom.client.ClientUtils;
import et.safaricom.commons.response.CallbackError;
import et.safaricom.commons.response.CallbackResponse;
import et.safaricom.commons.response.SuccessResponse;
import et.safaricom.constants.Endpoints;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.exceptions.MpesaSdkException;
import et.safaricom.transaction.request.AccountBalanceRequest;
import et.safaricom.transaction.request.PayOutRequest;
import et.safaricom.transaction.request.TransactionReversalRequest;
import et.safaricom.transaction.request.TransactionStatusRequest;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The {@code Transaction} class provides methods for initiating and managing
 * transactions with the M-Pesa API.
 */
public class Transaction {
    private final ClientUtils clientUtils;

    public Transaction(Authorization authorization, Client client, Logger logger) {
        this.clientUtils = new ClientUtils(client, authorization, logger);
    }

    /**
     * Executes a synchronous payout request to the M-Pesa API.
     *
     * @param payOutRequest the {@link PayOutRequest} containing the payout details.
     * @return the {@link SuccessResponse} with the result of the payout
     *         transaction.
     * @throws MpesaSdkException if any exception occurs during request processing.
     * @throws IOException       if an input or output exception occurs during the
     */
    public SuccessResponse payOutSync(PayOutRequest payOutRequest) throws IOException, MpesaApiException {
        return clientUtils.processSyncRequest(Endpoints.PAY_OUT.getValue(), payOutRequest, SuccessResponse.class);
    }

    /**
     * Executes a synchronous request to the M-Pesa API to query the status of a
     * transaction.
     *
     * @param transactionStatusRequest the {@link TransactionStatusRequest}
     *                                 containing the transaction details.
     * @return the {@link SuccessResponse} with the result of the transaction
     *         status query.
     * @throws MpesaSdkException if any exception occurs during request processing.
     * @throws IOException       if an input or output exception occurs during the
     */
    public SuccessResponse transactionStatusSync(TransactionStatusRequest transactionStatusRequest)
            throws IOException, MpesaApiException {
        return clientUtils.processSyncRequest(Endpoints.TRANSACTION_STATUS.getValue(), transactionStatusRequest,
                SuccessResponse.class);
    }

    /**
     * Executes a synchronous request to the M-Pesa API to reverse a transaction
     *
     * @param transactionReversalRequest the {@link TransactionReversalRequest}
     *                                   containing the transaction reversal
     *                                   details.
     * @return the {@link SuccessResponse} with the result of the transaction
     *         reversal.
     * @throws MpesaSdkException if any exception occurs during request processing.
     * @throws IOException       if an input or output exception occurs during the
     */
    public SuccessResponse transactionReversalSync(TransactionReversalRequest transactionReversalRequest)
            throws IOException, MpesaApiException {
        return clientUtils.processSyncRequest(Endpoints.TRANSACTION_REVERSAL.getValue(), transactionReversalRequest,
                SuccessResponse.class);
    }

    /**
     * Executes a synchronous request to the M-Pesa API to query the balance of the
     * short code.
     *
     * @param accountBalanceRequest the {@link AccountBalanceRequest} containing the
     *                              transaction details.
     * @return the {@link SuccessResponse} with the result of the account balance
     *         query.
     * @throws MpesaSdkException if any exception occurs during request processing.
     * @throws IOException       if an input or output exception occurs during the
     */
    public SuccessResponse accountBalanceSync(AccountBalanceRequest accountBalanceRequest)
            throws IOException, MpesaApiException {
        return clientUtils.processSyncRequest(Endpoints.ACCOUNT_BALANCE.getValue(), accountBalanceRequest, SuccessResponse.class);
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
        clientUtils.processASyncRequest(Endpoints.PAY_OUT.getValue(), payOutRequest, SuccessResponse.class, callbackResponse,
                callbackError);
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
        clientUtils.processASyncRequest(Endpoints.TRANSACTION_STATUS.getValue(), transactionStatusRequest, SuccessResponse.class,
                callbackResponse, callbackError);
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
        clientUtils.processASyncRequest(Endpoints.TRANSACTION_REVERSAL.getValue(), transactionReversalRequest,
                SuccessResponse.class, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the M-Pesa API to query the balance of a
     * short code.
     *
     * @param accountBalanceRequest the {@link AccountBalanceRequest} containing the
     *                              transaction details.
     * @param callbackResponse      the callback to be executed on successful
     *                              completion of the request.
     * @param callbackError         the callback to be executed if an error
     *                              occurs during request processing.
     */
    public void accountBalanceAsync(AccountBalanceRequest accountBalanceRequest,
            CallbackResponse<SuccessResponse> callbackResponse, CallbackError callbackError) {
        clientUtils.processASyncRequest(Endpoints.ACCOUNT_BALANCE.getValue(), accountBalanceRequest, SuccessResponse.class,
                callbackResponse, callbackError);
    }
}
