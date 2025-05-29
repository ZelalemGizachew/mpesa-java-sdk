package et.safaricom.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.Callback;

import java.io.IOException;

/**
 * Interface for making HTTP requests to the M-Pesa API. This interface provides
 * methods for sending both synchronous and asynchronous HTTP requests.
 */
public interface Client {

	/**
	 * Sends a synchronous HTTP request using the provided RequestClient object
	 * and returns the server's response.
	 *
	 * @param <T>           the type of the request body
	 * @param requestClient the client object containing request details such as
	 *                      URL, method, headers, and body
	 * @return the server's response as a Response object
	 * @throws IOException    if an input or output exception occurs during the
	 *                        request body is null
	 */
	<T> ResponseClient sendSyncRequest(RequestClient<T> requestClient) throws IOException;


	<T> void sendAsyncRequest(RequestClient<T> requestClient, CallbackClient callbackClient, CallbackErrorClient callbackErrorClient) throws JsonProcessingException;

}
