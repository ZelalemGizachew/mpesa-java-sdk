package et.safaricom.payment.request.validation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * Request body for validating a payment.
 */
@Data
public class ValidationConfirmationRequest {
    private Body body;

    public ValidationConfirmationRequest(C2BPaymentValidationRequest c2BPaymentValidationRequest) {
        this.body = new Body();
        this.body.setC2BPaymentValidationRequest(c2BPaymentValidationRequest);
    }
}

@Data
class Body {
    @JacksonXmlProperty(localName = "C2BPaymentValidationRequest", namespace = "http://cps.huawei.com/cpsinterface/c2bpayment")
    private C2BPaymentValidationRequest c2BPaymentValidationRequest;
}
