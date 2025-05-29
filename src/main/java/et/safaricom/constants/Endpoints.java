package et.safaricom.constants;

import lombok.Getter;

/**
 * Enum representing various API endpoints for interacting with the M-PESA service.
 * Each enum constant corresponds to a specific API operation and contains the
 * relative URL path for that endpoint.
 */
@Getter
public enum Endpoints {
    /**
     * API endpoint for generating a client credentials access token.
     */
    AUTH("/v1/token/generate?grant_type=client_credentials"),
    /**
     * API endpoint for initiating a customer-to-business (C2B) payment by sending a USSD Push request
     * to the M-PESA API, prompting the customer to complete the transaction by entering
     * their M-PESA PIN.
     */
    PUSH_CHECKOUT("/mpesa/stkpush/v3/processrequest"),
    /**
     * API endpoint for registering validation and confirmation URLs with M-PESA for payment notifications.
     */
    REGISTER_URL("/v1/c2b-register-url/register"),
    /**
     * API endpoint for executing a synchronous payout request to the M-Pesa API.
     */
    PAY_OUT("/mpesa/b2c/v2/paymentrequest"),
    /**
     * API endpoint for executing a synchronous request to the M-Pesa API to query the status of a
     * transaction.
     */
    TRANSACTION_STATUS("/mpesa/transactionstatus/v1/query"),
    /**
     * API endpoint for executing a synchronous request to the M-Pesa API to reverse a transaction.
     */
    TRANSACTION_REVERSAL("/mpesa/reversal/v2/request"),
    /**
     * API endpoint for executing a synchronous request to the M-Pesa API to query the balance of a
     * business account.
     */
    ACCOUNT_BALANCE("/mpesa/accountbalance/v2/query"),
    /**
     * API endpoint for confirming a processed payment after validation, notifying the business of the
     * transaction completion.
     */
    CONFIRM("/mpesa/internal/c2b/v1/confirm"),
    /**
     * API endpoint for validating an incoming payment request by checking transaction details against
     * predefined criteria.
     */
    VALIDATE("/mpesa/internal/c2b/v1/validate"),
    /**
     *
     */
    SIMULATE("/mpesa/b2c/simulatetransaction/v1/request");
    private final String value;



    Endpoints(String value) {
        this.value = value;
    }
}
