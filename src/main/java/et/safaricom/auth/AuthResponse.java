package et.safaricom.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {

    /**
     * Access token to access other APIs.
     * Type: String
     * Example: c9SQxWWhmdVRlyh0zh8gZDTkubVF
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * Type of the token.
     * Type: String
     * Example: Bearer
     */
    @JsonProperty("token_type")
    private String tokenType;

    /**
     * Token expiry time in seconds.
     * Type: String
     * Example: 3599
     */
    @JsonProperty("expires_in")
    private String expiresIn;
}
