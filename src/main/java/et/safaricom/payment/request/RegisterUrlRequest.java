package et.safaricom.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

/**
 * Request body for the register url request
 */
@Generated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUrlRequest {
    /**
     * unique number is tagged to an M-PESA pay bill/till number of the organization
     */
    @NotBlank
    @JsonProperty("ShortCode")
    private String shortCode;

    /**
     * This parameter defines the action if the validation URL is unreachable:
     * - "Completed" means the transaction is automatically completed, and
     * - "Cancelled" means it is canceled if M-PESA can't reach the URL.
     */
    @NotBlank
    @JsonProperty("ResponseType")
    private String responseType;

    /**
     * Use “RegisterURL” to differentiate the service from other services.
     */
    @NotBlank
    @JsonProperty("CommandID")
    private String commandID;

    /**
     * This is the URL that receives the confirmation request from API upon payment completion.
     */
    @URL
    @JsonProperty("ConfirmationURL")
    private String confirmationURL;

    /**
     * This URL receives the validation request upon payment submission,
     * triggered only if external validation is enabled for the shortcode.
     */
    @URL
    @JsonProperty("ValidationURL")
    private String validationURL;
}
