package et.safaricom.transaction.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

/**
 * The request body for getting a transaction status.
 */
@Builder
@Data
public class TransactionStatusRequest {

    /**
     * The name of the API initiator initiating the request.
     * This is the credential/username used to authenticate the transaction request.
     * Type: String
     */
    @JsonProperty("Initiator")
    @NotNull(message = "Initiator cannot be null")
    @Size(min = 1, message = "Initiator must not be empty")
    private String initiator;

    /**
     * Encrypted credential of the API user getting transaction status.
     * This is the encrypted password for the initiator to authenticate the transaction request.
     * Type: String
     */
    @JsonProperty("SecurityCredential")
    @NotNull(message = "SecurityCredential cannot be null")
    @Size(min = 1, message = "SecurityCredential must not be empty")
    private String securityCredential;

    /**
     * Command ID for the transaction status query.
     * Only 'TransactionStatusQuery' is valid.
     * Type: String
     * Example: TransactionStatusQuery
     */
    @JsonProperty("CommandID")
    @NotNull(message = "CommandID cannot be null")
    @Size(min = 1, message = "CommandID must not be empty")
    private String commandId;

    /**
     * Unique identifier to identify a transaction on M-PESA.
     * Type: Alpha-Numeric
     * Example: RHJ4BTOYS8
     */
    @JsonProperty("TransactionID")
    @NotNull(message = "TransactionID cannot be null")
    @Size(min = 1, message = "TransactionID must not be empty")
    private String transactionId;

    /**
     * This is a global unique identifier for the transaction request returned by the API proxy
     * upon successful request submission. If you don’t have the M-PESA transaction ID,
     * you can use this to query.
     * Type: String
     * Example: AG_20190826_0000777ab7d848b9e721
     */
    @JsonProperty("OriginalConcersationID")
    @NotNull(message = "OriginalConversationID cannot be null")
    @Size(min = 1, message = "OriginalConversationID must not be empty")
    private String originalConversationId;

    /**
     * Organization/MSISDN receiving the transaction.
     * Type: Numeric
     * Example: Shortcode / MSISDN (12 Digits)
     */
    @JsonProperty("PartyA")
    @NotNull(message = "PartyA cannot be null")
    @Pattern(regexp = "\\d{1,20}", message = "PartyA must be a number between 1 and 20 digits")
    private String partyA;

    /**
     * Type of organization receiving the transaction.
     * Type: Numeric
     * Example: 4 – Organization shortcode
     */
    @JsonProperty("IdentifierType")
    @NotNull(message = "IdentifierType cannot be null")
    @Size(min = 1, message = "IdentifierType must not be empty")
    private String identifierType;

    /**
     * The URL to be specified in your request that will be used by M-PESA
     * to send a callback upon processing of the request.
     * Type: URL
     * Example: https://ip/ or domain:port/path
     */
    @JsonProperty("ResultURL")
    @NotNull(message = "ResultURL cannot be null")
    @URL(message = "ResultURL must be a valid URL")
    private String resultUrl;

    /**
     * The URL to be specified in your request that will be used by API Proxy
     * to send notification in case the payment request is timed out while awaiting processing in the queue.
     * Type: URL
     * Example: https://ip:port or domain:port/path
     */
    @JsonProperty("QueueTimeOutURL")
    @NotNull(message = "QueueTimeOutURL cannot be null")
    @URL(message = "QueueTimeOutURL must be a valid URL")
    private String queueTimeoutUrl;

    /**
     * Any additional information to be associated with the transaction. (optional)
     * Type: String
     * Example: A sequence of characters up to 100
     */
    @JsonProperty("Remarks")
    @Size(max = 100, message = "Remarks must be up to 100 characters")
    private String remarks;

    /**
     * Any additional information to be associated with the transaction. (optional)
     * Type: String
     * Example: A sequence of characters up to 100
     */
    @JsonProperty("Occasion")
    @Size(max = 100, message = "Occasion must be up to 100 characters")
    private String occasion;
}
