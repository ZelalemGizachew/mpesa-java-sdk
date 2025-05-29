package et.safaricom.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing the response of a simulating payment transaction.
 */
@Data
public class SimulateResponse {
    /**
     * Global unique identifier for the transaction request returned by the M-PESA core transaction system upon successful request submission.
     */
    @JsonProperty("ConversationID")
    private String conversationID;

    /**
     * Global unique identifier for the transaction request returned by the M-PESA API proxy upon successful request submission.
     */
    @JsonProperty("OriginatorConversationID")
    private String originatorConversationID;

    /**
     * Numeric status code that indicates the status of the transaction processing.
     * 0 means success, and any other code means an error occurred or the transaction failed.
     */
    @JsonProperty("ResponseCode")
    private String responseCode;

    /**
     * Description of the request submission status.
     */
    @JsonProperty("ResponseDescription")
    private String responseDescription;
}
