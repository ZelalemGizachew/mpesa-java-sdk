package et.safaricom.client;

import et.safaricom.constants.MediaType;
import lombok.*;

/**
 * Response Client to hold the status code and body of the response
 */
@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseClient {
    private int statusCode;
    private String body;
    @Builder.Default
    private String contentType = MediaType.JSON.getValue();
}
