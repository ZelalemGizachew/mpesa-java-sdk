package et.safaricom.payment.request.validation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

/**
 * Request body for confirm a payment.
 */
@Data
public class ConfirmationRequest {
    private ConfirmationBody body;

    public ConfirmationRequest(C2BPaymentValidationRequest c2BPaymentValidationRequest) {
        this.body = new ConfirmationBody();
        this.body.setC2BPaymentValidationRequest(c2BPaymentValidationRequest);
    }
}

@Data
class ConfirmationBody {
    @JacksonXmlProperty(localName = "C2BPaymentConfirmationRequest", namespace = "http://cps.huawei.com/cpsinterface/c2bpayment")
    private C2BPaymentValidationRequest c2BPaymentValidationRequest;
}
