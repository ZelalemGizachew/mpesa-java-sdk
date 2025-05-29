package et.safaricom.payment.response.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Holds more details for the transaction.
 * only returned for successful transaction
 */
@Getter
@Setter
class CallbackMetadata {
    @JsonProperty("Item")
    private List<Item> items;
}
