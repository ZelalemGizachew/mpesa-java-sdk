package et.safaricom.client;

import et.safaricom.constants.MediaType;
import lombok.*;

import java.util.Map;
import java.util.logging.Logger;

/**
 *  Request client for making HTTP requests.
 * @param <T> the type of the request body
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestClient<T> {
    private String method;
    private String url;
    private T body;
    private MediaType contentType;
    private String authorization;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private Logger logger;

}