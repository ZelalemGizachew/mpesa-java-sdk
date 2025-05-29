package et.safaricom.payment.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response body for the confirmation request.
 */
@Getter
@Setter
public class ConfirmationResponse {
    private String resultCode;
    private String resultDesc;
}
