package et.safaricom.config.spring.boot;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.logging.Level;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "mpesa.sdk")
public class MpesaProperties {
    /**
     * The consumer key provided by Safaricom.
     */
    private String consumerKey;
    /**
     * The consumer secret provided by Safaricom.
     */
    private String consumerSecret;
    /**
     * The request timeout in milliseconds.
     */
    private int connectionTimeout = 5000;
    /**
     * The number of times to retry a request in case of failure.
     */
    private int maxRetries = 1;

    /**
     * Whether to retry a request in case of connection failure.
     */
    private boolean retryOnConnectionFailure = false;
    /**
     * The base URL of the M-Pesa API.
     */
    @Setter
    private String baseUrl = "https://apisandbox.safaricom.et";

    /**
     * The logging level to use for selectively displaying logs
     */
    @Setter
    private String logLevel = "OFF";

    public Level getLogLevel() {
        try {
            return Level.parse(logLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Level.OFF;
        }
    }
}
