package et.safaricom.payment.response.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the callback response body.
 */
@Getter
@Setter
public class CallbackResponse {
    @JsonProperty("Body")
    private Body body;
}
