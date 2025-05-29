package et.safaricom.payment.request.validation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C2BPaymentValidationRequest {
    /**
     * The transaction type specified during the payment request.
     * Example: Buy Goods or Pay Bill.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "TransType")
    private String transType;

    /**
     * Unique M-Pesa transaction ID for every payment request.
     * Example: LHG31AA5TX.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "TransID")
    private String transID;

    /**
     * Timestamp of the transaction in the format YYYYMMDDHHMMSS.
     * Example: 20240904T123000.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "TransTime")
    private String transTime;

    /**
     * Amount transacted, money paid by the customer.
     * Only whole numbers are supported. Example: 100.
     */
    @NotNull
    @Pattern(regexp = "^\\d+(\\.\\d)?$", message = "Invalid amount format")
    @JacksonXmlProperty(localName = "TransAmount")
    private String transAmount;

    /**
     * Organization's shortcode (Paybill or Buygoods).
     * A 5 to 6-digit account number. Example: 123456.
     */
    @Size(min = 6, max = 6)
    @Digits(integer = 6, fraction = 0)
    @NotNull
    @JacksonXmlProperty(localName = "BusinessShortCode")
    private String businessShortCode;

    /**
     * Account number for which the customer is making the payment.
     * Applicable for Customer PayBill Transactions. Example: Bill123.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "BillRefNumber")
    private String billRefNumber;

    /**
     * Invoice number for the payment. Example: Invoice123.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "InvoiceNumber")
    private String invoiceNumber;

    /**
     * Current utility account balance of the receiving organization shortcode.
     * For validation requests, this is usually blank.
     * Example: 30671.
     */
    @JacksonXmlProperty(localName = "OrgAccountBalance")
    private String orgAccountBalance;

    /**
     * A transaction ID the partner can use to identify the transaction.
     * Example: TP123456.
     */
    @NotBlank
    @NotNull
    @JacksonXmlProperty(localName = "ThirdPartyTransID")
    private String thirdPartyTransID;

    /**
     * Masked mobile number of the customer making the payment.
     * Example: 25170****149.
     */
    @NotNull
    @NotBlank
    @JacksonXmlProperty(localName = "MSISDN")
    private String msisdn;

    /**
     * Customer's first name as per the M-Pesa register.
     * This parameter can be empty. Example: John.
     */
    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;

    /**
     * Customer's middle name as per the M-Pesa register.
     * This parameter can be empty. Example: Doe.
     */
    @JacksonXmlProperty(localName = "MiddleName")
    private String middleName;

    /**
     * Customer's last name as per the M-Pesa register.
     * This parameter can be empty. Example: Smith.
     */
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;

    /**
     * A list of key-value pairs representing Know Your Customer (KYC) information.
     */
    @NotEmpty
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "KYCInfo")
    private List<KYCInfo> kycInfo;
}
