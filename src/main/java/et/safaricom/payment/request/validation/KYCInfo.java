package et.safaricom.payment.request.validation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KYCInfo {
    @JacksonXmlProperty(localName = "KYCValue")
    private String kycValue;
}
