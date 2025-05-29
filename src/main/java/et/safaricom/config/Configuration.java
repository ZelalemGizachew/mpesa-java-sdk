package et.safaricom.config;

import lombok.*;

import java.util.logging.Level;

/**
 * Configuration class provides default configuration settings for interacting
 * with the M-Pesa API. This includes API details such as version, timeouts, retry
 * settings, base URL, and logging level.
 * <p>
 * The class is immutable and provides default values for all fields. These
 * defaults can be used in instances where specific configuration is not
 * provided by the client.
 */
@Generated
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    /**
     * The API version to use.
     */
    @Builder.Default
    private String version = "v1";
    /**
     * The request timeout in milliseconds.
     */
    @Builder.Default
    private int connectionTimeout = 5000;
    /**
     * The number of times to retry a request in case of failure.
     */
    @Builder.Default
    private int maxRetries = 1;

    /**
     * Whether to retry a request in case of connection failure.
     */
    @Builder.Default
    private boolean retryOnConnectionFailure = false;
    /**
     * The base URL of the M-Pesa API.
     */
    @Builder.Default
    @Setter
    private String baseUrl = "https://apisandbox.safaricom.et";

    /**
     * The logging level to use for selectively displaying logs
     */
    @Builder.Default
    @Setter
    private Level logLevel = Level.OFF;
}
