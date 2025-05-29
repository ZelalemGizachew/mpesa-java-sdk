package et.safaricom.exceptions;

import et.safaricom.commons.response.MpesaError;
import lombok.Getter;


/**
 * Exception thrown by the SDK when an error occurs when calling
 * the M-Pesa API. This exception wraps the MpesaError object which
 * contains the error code and message returned by the API.
 */
@Getter
public class MpesaApiException extends Exception {

    /**
     * The MpesaError object containing the error code and message returned
     * by the M-Pesa API.
     */
    MpesaError mpesaError;

    public MpesaApiException(String message, MpesaError mpesaError) {
        super(message);
        this.mpesaError = mpesaError;
    }

}
