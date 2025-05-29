package et.safaricom.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the request parameters for a transaction response.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulateRequest {
    /**
     * Unique Command ID identifying the type of transaction.
     */
    @JsonProperty("CommandID")
    @NotBlank
    private String commandID;

    /**
     * Transaction amount as a numeric string.
     */
    @JsonProperty("Amount")
    @NotBlank
    @Pattern(regexp = "\\d+", message = "Amount must be numeric")
    private String amount;

    /**
     * Phone number of the customer as a numeric string.
     */
    @JsonProperty("Msisdn")
    @NotBlank
    @Pattern(regexp = "\\d+", message = "Msisdn must be numeric")
    private String msisdn;

    /**
     * Unique reference number associated with the bill.
     */
    @JsonProperty("BillRefNumber")
    @NotBlank
    private String billRefNumber;

    /**
     * Unique short code of the organization.
     */
    @JsonProperty("ShortCode")
    @NotBlank
    @Pattern(regexp = "\\d+", message = "ShortCode must be numeric")
    private String shortCode;
}
