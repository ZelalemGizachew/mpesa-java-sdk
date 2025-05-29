package et.safaricom.exceptions;

/**
 * Exception thrown by the SDK when an error occurs while validating user input.
 * The exception wraps a message that describes the error(s) that occurred during
 * validation.
 */
public class DataValidationException extends Exception {
    /**
     * Creates an instance of the exception with the specified message.
     *
     * @param message the message describing the error(s) that occurred during validation.
     */
    public DataValidationException(String message) {
        super(message);
    }
}
