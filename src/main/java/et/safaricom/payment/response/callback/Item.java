package et.safaricom.payment.response.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
/**
 * Represents an item in the CallbackMetadata.
 */
@Getter
@Setter
public class Item {
    /**
     * Name of the metadata item.
     */
    @JsonProperty("Name")
    private String name;

    /**
     * Value of the metadata item.
     */
    @JsonProperty("Value")
    private Object value;
}
