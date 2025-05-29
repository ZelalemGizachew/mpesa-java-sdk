package et.safaricom.auth;

import lombok.Data;

/**
 * Represents an authentication error response with possible error codes and their details:
 * <ul>
 *     <li>999991: Invalid client id passed - Incorrect basic Authorization username.</li>
 *     <li>999996: Invalid Authentication passed - Incorrect authorization type. </li>
 *     <li>999997: Invalid Authorization Header - Incorrect basic authorization password.</li>
 *     <li>999998: Required parameter [grant_type] is invalid or empty - Incorrect grant type.</li>
 * </ul>
 */
@Data
public class AuthErrorResponse {
    /**
     * The result code for the authentication response.
     */
    private String resultCode;
    
    /**
     * A brief description of the result of the authentication response.
     */
    private String resultDesc;
}
