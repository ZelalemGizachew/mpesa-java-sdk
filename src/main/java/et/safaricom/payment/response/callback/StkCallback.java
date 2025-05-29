package et.safaricom.payment.response.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the callback response body.
 */
@Getter
@Setter
public class StkCallback {

    /**
     * A unique identifier for the submitted payment request.
     */
    @JsonProperty("MerchantRequestID")
    private String merchantRequestID;

    /**
     * A globally unique identifier of the processed checkout transaction request.
     */
    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestID;

    /**
     * Numeric status code indicating the status of the transaction processing.
     */
    @JsonProperty("ResultCode")
    private Integer resultCode;

    /**
     * Description of the result status.
     */
    @JsonProperty("ResultDesc")
    private String resultDesc;

    /**
     * Holds more details for the transaction.
     * only returned for successful transaction
     */
    @JsonProperty("CallbackMetadata")
    private CallbackMetadata callbackMetadata;
}

