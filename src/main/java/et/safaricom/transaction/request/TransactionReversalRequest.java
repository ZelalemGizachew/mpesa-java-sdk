package et.safaricom.transaction.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.URL;

/**
 * Request Body to make a transaction reversal request.
 */
@Builder
@Data
public class TransactionReversalRequest {

	/**
	 * Unique identifier for the transaction request.
	 * Type: String
	 */
	@JsonProperty("OriginatorConversationID")
	@NotNull(message = "OriginatorConversationID cannot be null")
	@Size(min = 1, message = "OriginatorConversationID must not be empty")
	private String originatorConversationId;

	/**
	 * Original conversion id
	 * Type: String
	 */
	@JsonProperty("OriginalConversationID")
	@NotNull(message = "OriginalConversationID cannot be null")
	@Size(min = 1, message = "OriginalConversationID must not be empty")
	private String originalConversationId;
	/**
	 * The name of the initiator to initiate the request.
	 * This is the credential/username used to authenticate the transaction request.
	 * Type: Alpha-Numeric
	 */
	@JsonProperty("Initiator")
	@NotNull(message = "Initiator cannot be null")
	@Size(min = 1, message = "Initiator must not be empty")
	private String initiator;

	/**
	 * Encrypted credential of the user getting the transaction amount.
	 * This is the encrypted password for the initiator to authenticate the transaction request.
	 * Type: String
	 */
	@JsonProperty("SecurityCredential")
	@NotNull(message = "SecurityCredential cannot be null")
	@Size(min = 1, message = "SecurityCredential must not be empty")
	private String securityCredential;

	/**
	 * Command ID for the transaction. Only 'TransactionReversal' is valid.
	 * Type: String
	 * Example: TransactionReversal
	 */
	@JsonProperty("CommandID")
	@NotNull(message = "CommandID cannot be null")
	@Size(min = 1, message = "CommandID must not be empty")
	private String commandId;

	/**
	 * The unique identifier for the transaction being reversed.
	 * Type: String
	 * Example: LKXXXX1234
	 */
	@JsonProperty("TransactionID")
	@NotNull(message = "TransactionID cannot be null")
	@Size(min = 1, message = "TransactionID must not be empty")
	private String transactionId;

	/**
	 * The amount of money involved in the transaction.
	 * Type: String
	 */
	@JsonProperty("Amount")
	@NotNull(message = "Amount cannot be null")
	@Min(value = 1, message = "Amount must be greater than 0")
	private String amount;

	/**
	 * The organization that receives the transaction.
	 * Type: Numeric
	 * Example: Shortcode (6-9 digits)
	 */
	@JsonProperty("ReceiverParty")
	@NotNull(message = "ReceiverParty cannot be null")
	@Pattern(regexp = "\\d{6,9}", message = "ReceiverParty must be a 6-9 digit number")
	private String receiverParty;

	/**
	 * The type of organization that receives the transaction.
	 * Type: String
	 * Example: 11
	 */
	@JsonProperty("RecieverIdentifierType")
	@NotNull(message = "ReceiverIdentifierType cannot be null")
	@Size(min = 1, message = "ReceiverIdentifierType must not be empty")
	private String receiverIdentifierType;

	/**
	 * The path that stores information about the transaction result.
	 * Type: URL
	 * Example: https://ip or domain:port/path
	 */
	@JsonProperty("ResultURL")
	@NotNull(message = "ResultURL cannot be null")
	@URL(message = "ResultURL must be a valid URL")
	private String resultUrl;

	/**
	 * The path that stores information about the timeout transaction.
	 * Type: URL
	 * Example: https://ip or domain:port/path
	 */
	@JsonProperty("QueueTimeOutURL")
	@NotNull(message = "QueueTimeOutURL cannot be null")
	@URL(message = "QueueTimeOutURL must be a valid URL")
	private String queueTimeOutUrl;

	/**
	 * Comments that are sent along with the transaction.
	 * Type: String
	 * Example: test
	 */
	@JsonProperty("Remarks")
	@Size(max = 100, message = "Remarks must be up to 100 characters")
	private String remarks;

	/**
	 * Optional parameter to provide additional information.
	 * Type: String
	 * Example: Sequence of characters up to 100
	 */
	@JsonProperty("Occasion")
	@Size(max = 100, message = "Occasion must be up to 100 characters")
	private String occasion;
}
