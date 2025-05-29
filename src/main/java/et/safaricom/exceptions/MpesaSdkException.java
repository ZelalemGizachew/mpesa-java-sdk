package et.safaricom.exceptions;

/**
 * A generic exception thrown by the M-Pesa SDK for Java.
 * <p>
 * This exception is thrown when an unexpected error occurs while interacting with the M-Pesa API.
 * It may be caused by a network error, a configuration error, or an error in the SDK itself.
 */
public class MpesaSdkException extends RuntimeException {
    /**
     * The original exception that was thrown.
     */
    Exception exception;

    /**
     * The M-Pesa API error response.
     */
    MpesaApiException mpesaError;
    /**
     * Constructor for MpesaSdkException.
     * @param message error message
     */
    public MpesaSdkException(String message) {
        super(message);
    }

    /**
     * Constructor for MpesaSdkException.
     * @param message error message
     * @param exception original exception
     */
    public MpesaSdkException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }

}
