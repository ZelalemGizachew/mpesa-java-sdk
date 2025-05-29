package et.safaricom.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * An interceptor to retry requests in case of IOException
 */
public class RetryInterceptor implements Interceptor {
    private final int maxRetries;

    /**
     * Constructs a new instance of the {@link RetryInterceptor} class.
     *
     * @param maxRetries the maximum number of retries to be attempted in case
     *                   of IOException
     */
    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Intercepts the request and retries it up to {@code maxRetries} times in case
     * of
     * IOException. If the request is successful, the response is returned. If the
     * request
     * fails after the maximum number of retries, the caught IOException is
     * re-thrown.
     *
     * @param chain the interceptor chain
     * @return the server's response
     * @throws IOException if an input or output exception occurs during the request
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException exception = null;
        Response response = null;

        // Retry the request up to maxRetries
        int i = 0;
        do {
            try {
                response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                exception = e;
            }
            i++;
        } while (i < maxRetries);

        if (exception != null) {
            throw exception;
        }

        return response;
    }
}
