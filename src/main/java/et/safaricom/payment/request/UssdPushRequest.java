package et.safaricom.payment.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import et.safaricom.utils.ValidPhoneNumber;
import jakarta.validation.constraints.*;
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
public class UssdPushRequest {
    /**
     * A unique ID for the payment request.
     */
    @NotBlank
    @JsonProperty("MerchantRequestID")
    private String merchantRequestId;
    /**
     * The shortcode of the business receiving the payment.
     */
    @Digits(integer = 6, fraction = 0)
    @NotNull
    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    /**
     * The passkey used to generate password
     */
    @NotBlank
    private String passkey;

    /**
     * Defines the type of transaction (e.g., CustomerPayBillOnline, CustomerBuyGoodOnline).
     */
    @NotBlank
    @JsonProperty("TransactionType")
    private String transactionType;

    /**
     * The amount to be transacted (numeric).
     */
    @NotNull
    @Pattern(regexp = "^\\d+(\\.\\d)?$", message = "Invalid amount format")
    @JsonProperty("Amount")
    private String amount;

    /**
     * The phone number initiating the payment (M-PESA registered number).
     */
    @ValidPhoneNumber
    @JsonProperty("PartyA")
    private String partyA;
    /**
     * The receiving shortcode or business (6 digits).
     */
    @Digits(integer = 6, fraction = 0)
    @NotNull
    @JsonProperty("PartyB")
    private String partyB;

    /**
     * The phone number to receive the STK PIN prompt (M-PESA registered).
     */
    @ValidPhoneNumber
    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    /**
     * A description of the transaction.
     */
    @NotBlank
    @JsonProperty("TransactionDesc")
    private String transactionDesc;

    /**
     * The URL to receive the callback notification after the transaction is processed.
     */
    @URL
    @JsonProperty("CallBackURL")
    private String callBackUrl;

    /**
     * An alphanumeric reference for the transaction (max 12 characters).
     */
    @NotBlank
    @JsonProperty("AccountReference")
    private String accountReference;

    /**
     * A JSON object containing additional details like BundleName, BundleType, etc.
     */
    @NotNull
    @JsonProperty("ReferenceData")
    private List<ReferenceData> referenceData;
}
