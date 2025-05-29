package et.safaricom.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.utils.ValidPhoneNumber;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * STK Push Request
 */
@Generated
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UssdPushApiRequest extends UssdPushRequest {
    /**
     * The base64 encoded password used to encrypt the request (Shortcode + Passkey + Timestamp).
     */
    @JsonProperty("Password")
    private String password;

    /**
     * The timestamp of the transaction in the format YYYYMMDDHHMMSS.
     */
    @JsonProperty("Timestamp")
    private String timestamp;
}
