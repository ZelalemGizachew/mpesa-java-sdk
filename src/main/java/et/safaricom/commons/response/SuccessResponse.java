package et.safaricom.commons.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The success response body.
 */
@Data
public class SuccessResponse {
	@JsonProperty("ConversationID")
	private String conversationID;

	@JsonProperty("OriginatorConversationID")
	private String originatorConversationID;

	@JsonProperty("ResponseCode")
	private String responseCode;

	@JsonProperty("ResponseDescription")
	private String responseDescription;
}
