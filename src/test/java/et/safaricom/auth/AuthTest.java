package et.safaricom.auth;

import static org.junit.jupiter.api.Assertions.*;

import et.safaricom.Mpesa;
import org.junit.jupiter.api.Test;

public class AuthTest {

	/**
	 * Tests the authentication process by verifying that the access token
	 * is successfully generated and is not null. This test initializes the
	 * Mpesa object using environment variables for consumer key and secret,
	 * and asserts that the access token is present.
	 */
	@Test
	void testAuthTest() {
		// Arrange
		Mpesa mpesa = new Mpesa(
				System.getenv("CONSUMER_KEY"),
				System.getenv("CONSUMER_SECRET"));

		// Assert
		assertNotNull(mpesa.getAuthorization().getAccessToken(), "Access token should not be null");
	}

}
