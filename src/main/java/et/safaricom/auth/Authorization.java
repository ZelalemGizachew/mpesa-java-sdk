package et.safaricom.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import et.safaricom.client.Client;
import et.safaricom.client.RequestClient;
import et.safaricom.client.ResponseClient;
import et.safaricom.commons.response.MpesaError;
import et.safaricom.constants.Endpoints;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.exceptions.MpesaSdkException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * The {@code Authorization} class is responsible for managing the
 * authentication process with Safaricom's APIs. It includes methods
 * for generating basic authentication headers, requesting access tokens,
 * and managing token expiration.
 */
@Getter
public class Authorization {
    /**
     * The basic authentication header.
     */
    private final String basicAuth;
    /**
     * Access token to access other APIs
     */
    private String accessToken;
    /**
     * Type of Token
     */
    private String tokenType;
    /**
     * Token expiry time in seconds
     */
    private long expiresIn;
    /**
     * A timestamp indicating when the token was issued
     */
    private LocalDateTime issuedAt;
    private final Client client;
    private Logger log;
    private final ObjectMapper objectMapper;

    /**
     * Constructs an {@code Authorization} object with the provided credentials and client.
     * Automatically generates a basic authentication header and fetches an access token.
     * @param consumerKey The consumer key provided by Safaricom.
     * @param consumerSecret The consumer secret provided by Safaricom.
     * @param client The HTTP client used for making API requests.
     * @param logger The logger instance for logging debug information.
     */
    public Authorization(String consumerKey, String consumerSecret, Client client, Logger logger) {
        this(consumerKey, consumerSecret, client);
        this.log = logger;
    }

    /**
     * Constructs an {@code Authorization} object with the provided credentials and
     * client. Automatically generates a basic authentication header and fetches an access
     * token.
     *
     * @param consumerKey    The consumer key provided by Safaricom.
     * @param consumerSecret The consumer secret provided by Safaricom.
     * @param client         The HTTP client used for making API requests.
     */
    public Authorization(String consumerKey, String consumerSecret, Client client) {
        this.basicAuth = generateBasicAuth(consumerKey, consumerSecret);
        this.client = client;
        objectMapper = new ObjectMapper();
        updateAuth();
    }

    /**
     * Generates a basic authentication header using the provided consumer key and
     * secret. The header is Base64-encoded as required by the API.
     *
     * @param consumerKey    The consumer key.
     * @param consumerSecret The consumer secret.
     * @return A formatted string containing the basic authentication header.
     */
    public String generateBasicAuth(String consumerKey, String consumerSecret) {
        return "Basic " + Base64.getEncoder().encodeToString((consumerKey + ":" + consumerSecret).getBytes());
    }

    /**
     * Generates an access token using the client credentials provided.
     * This method makes a synchronous HTTP request to the authorization endpoint.
     *
     * @return An {@code AuthResponse} object containing the access token details.
     * @throws MpesaSdkException if the request fails or if the response body is null.
     */
    public AuthResponse generateAccessToken() {
        RequestClient<String> requestClient = RequestClient.<String>builder()
                .authorization(basicAuth)
                .url(Endpoints.AUTH.getValue())
                .method("GET")
                .build();

        try {
            ResponseClient response = client.sendSyncRequest(requestClient);
            if (response.getStatusCode() % 400 < 100) {
                AuthErrorResponse error = objectMapper.readValue(response.getBody(), AuthErrorResponse.class);
                throw new MpesaApiException(
                        error.getResultDesc(),
                        MpesaError.builder()
                                .errorCode(error.getResultCode())
                                .errorMessage(error.getResultDesc())
                                .build()
                );
            }
            if (response.getBody() == null) {
                throw new MpesaSdkException("Auth response body is null");
            }
            String responseBody = response.getBody();
            return objectMapper.readValue(responseBody, AuthResponse.class);
        } catch (Exception e) {
            throw new MpesaSdkException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the access token for API usage. This method should ensure the token
     * is valid by checking its expiration before returning.
     *
     * @return The access token as a {@code String}.
     */
    public String getAccessToken() {
        // Check if the token is valid and has not expired
        if (LocalDateTime.now().isAfter(issuedAt.plusSeconds(expiresIn))) {
            updateAuth();
        }
        return this.accessToken;
    }

    /**
     * Updates the authentication details by generating a new access token.
     * Sets the access token, token type, expiration time, and the issue timestamp.
     */
    public void updateAuth() {
        AuthResponse authResponse = generateAccessToken();
        this.issuedAt = LocalDateTime.now();
        this.accessToken = authResponse.getTokenType() + " " + authResponse.getAccessToken();
        this.tokenType = authResponse.getTokenType();
        this.expiresIn = Long.parseLong(authResponse.getExpiresIn());
    }
}
