package et.safaricom.utils;

import et.safaricom.commons.response.CallbackError;
import et.safaricom.exceptions.DataValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

/**
 * Utility class for validating input objects using the jakarta Validation API.
 * 
 */
public class DataValidation {

    /**
     * Validates the given input object using the provided Validator.
     * If any validation errors occur, a DataValidationException is thrown
     * with a message that concatenates all the error messages.
     * 
     * @param validator the Validator to use for validation
     * @param input     the object to validate
     * @throws DataValidationException if validation fails
     */
    public static void validateInput(Validator validator, Object input) throws DataValidationException {
        // Validate the input object using the provided Validator
        Set<ConstraintViolation<Object>> violations = validator.validate(input);

        // If there are any violations, throw a DataValidationException with the error
        // messages
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder("Validation failed:\n");
            for (ConstraintViolation<Object> violation : violations) {
                errorMessages.append(violation.getPropertyPath()).append(": ")
                        .append(violation.getMessage()).append("\n");
            }
            throw new DataValidationException(errorMessages.toString());
        }
    }

    public static void validateInput(Validator validator, Object input, CallbackError callbackError)  {
        try {
            validateInput(validator, input);
        } catch (DataValidationException e) {
            callbackError.call(e);
        }
    }
}
