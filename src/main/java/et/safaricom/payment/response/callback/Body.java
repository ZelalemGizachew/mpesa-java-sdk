package et.safaricom.payment.response.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Body {
    @JsonProperty("stkCallback")
    private StkCallback stkCallback;
}
