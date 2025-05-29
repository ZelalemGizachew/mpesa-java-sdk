package et.safaricom.payment.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Response body for the register url request
 */
@Data
public class RegisterUrlResponse {
    private Header header;
    private Object body;

    /**
     * The body of the response
     */
    @Data
    public static class Header {
        /**
         * Unique reference ID for the request.
         */
        private String requestRefId;
        /**
         * This indicates the response code.
         */
        private String responseCode;
        /**
         * Description about the API response.
         */
        private String responseMessage;
        /**
         * Description for the customer about the API response.
         */
        private String customerMessage;
        /**
         * Timestamp. e.g. 2024-02-12T02:20:31.390
         */
        private String timestamp;
    }
}

