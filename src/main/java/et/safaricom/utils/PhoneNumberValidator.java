package et.safaricom.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization required for this annotation
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false; // Invalid if null or empty
        }

        // Check if it starts with '7', '07', or '2517' and has valid length
        return phoneNumber.matches("^(7|07|2517)[0-9]{6,9}$");
    }
}