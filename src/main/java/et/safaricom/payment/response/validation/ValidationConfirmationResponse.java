package et.safaricom.payment.response.validation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Response body for the validation/confirmation request.
 */
@Setter
@Getter
public class ValidationConfirmationResponse {
    /**
     * A code indicating whether to complete the transaction.
     * 0 (zero) always means complete, other values mean cancel.
     */
    @JacksonXmlProperty(localName = "ResultCode")
    private String resultCode;

    /**
     * Short description of the validation/confirmation result.
     */
    @JacksonXmlProperty(localName = "ResultDesc")
    private String resultDesc;

    /**
     * Optional value to identify the payment during a confirmation callback.
     */
    @JacksonXmlProperty(localName = "ThirdPartyTransID")
    private String thirdPartyTransID;
}


