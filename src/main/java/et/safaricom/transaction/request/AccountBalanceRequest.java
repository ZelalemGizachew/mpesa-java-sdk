package et.safaricom.transaction.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * The request body for getting an account balance.
 */
@Builder
@Data
public class AccountBalanceRequest {

	/**
	 * The name of the API initiator initiating the request.
	 * Type: String (AlphaNumeric)
	 * Example: InitAccountBalance
	 */
	@JsonProperty("OriginatorConversationID")
	@NotNull(message = "OriginatorConversationID cannot be null")
	@Size(min = 1, message = "OriginatorConversationID must not be empty")
	private String originatorConversationID;

	/**
	 * The name of the API initiator initiating the request.
	 * Type: String (AlphaNumeric)
	 * Example: InitAccountBalance
	 */
	@JsonProperty("Initiator")
	@NotNull(message = "Initiator cannot be null")
	private String initiator;

	/**
	 * Encrypted credential of the API user requesting account balance.
	 * Type: String (AlphaNumeric)
	 * Example: EToK4lNRxdIhQjhPXi==
	 */
	@JsonProperty("SecurityCredential")
	@NotNull(message = "SecurityCredential cannot be null")
	private String securityCredential;

	/**
	 * Command ID to use. Only 'AccountBalance' is valid.
	 * Type: String
	 * Example: AccountBalance
	 */
	@JsonProperty("CommandID")
	@NotNull(message = "CommandID cannot be null")
	@Size(min = 1, message = "CommandID must not be empty")
	private String commandID;

	/**
	 * The shortcode of the organization querying for the account balance.
	 * Type: String (Numeric)
	 * Example: 101010
	 */
	@JsonProperty("PartyA")
	@NotNull(message = "PartyA cannot be null")
	@Pattern(regexp = "\\d+", message = "PartyA must be numeric")
	private String partyA;

	/**
	 * Type of organization querying for the account balance.
	 * Type: String (Numeric)
	 * Example: 4
	 */
	@JsonProperty("IdentifierType")
	@NotNull(message = "IdentifierType cannot be null")
	@Pattern(regexp = "\\d+", message = "IdentifierType must be numeric")
	private String identifierType;

	/**
	 * Comments that are sent along with the transaction.
	 * Type: String
	 * Example: Account Balance
	 */
	@JsonProperty("Remarks")
	@NotNull(message = "Remarks cannot be null")
	private String remarks;

	/**
	 * The endpoint that receives a timeout message.
	 * Type: String (URL)
	 * Example: https://ip:port/path or domain:port/path
	 */
	@JsonProperty("QueueTimeOutURL")
	@NotNull(message = "QueueTimeOutURL cannot be null")
	@URL(message = "QueueTimeOutURL must be a valid URL")
	private String queueTimeOutURL;

	/**
	 * This is the URL specified in your request to be used by M-PESA
	 * to send a callback upon processing of the request.
	 * Type: String (URL)
	 * Example: https://ip:port/path or domain:port/path
	 */
	@JsonProperty("ResultURL")
	@NotNull(message = "ResultURL cannot be null")
	@URL(message = "ResultURL must be a valid URL")
	private String resultURL;
}
