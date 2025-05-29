package et.safaricom.mpesasdkspring;

import et.safaricom.Mpesa;
import et.safaricom.exceptions.DataValidationException;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.payment.request.UssdPushRequest;
import et.safaricom.payment.response.UssdPushResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class MpesaSdkSpringApplication {
    private final Mpesa mpesa;

    public MpesaSdkSpringApplication(Mpesa mpesa) {
        this.mpesa = mpesa;
    }

    public static void main(String[] args) {
        SpringApplication.run(MpesaSdkSpringApplication.class, args);
    }

    @PostMapping("/stkpush")
    public UssdPushResponse stkpush(@RequestBody UssdPushRequest ussdPushRequest) throws DataValidationException, MpesaApiException {
        return mpesa.triggerUssdPush(ussdPushRequest);
    }
}
