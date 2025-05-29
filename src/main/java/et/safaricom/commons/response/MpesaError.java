package et.safaricom.commons.response;

import lombok.*;

/**
 * A class representing an error response from the M-Pesa API. The class contains properties
 * corresponding to the error code, error message, and request ID returned by the API.
 */
@Generated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MpesaError {
    /**
     * The request ID returned by the M-Pesa API.
     */
    public String requestId;

    /**
     * The error code returned by the M-Pesa API.
     */
    public String errorCode;

    /**
     * The error message returned by the M-Pesa API.
     */
    public String errorMessage;

}
