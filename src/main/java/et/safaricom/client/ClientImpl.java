package et.safaricom.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import et.safaricom.config.Configuration;
import et.safaricom.exceptions.MpesaSdkException;
import et.safaricom.utils.LoggerUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Implementation of the {@link Client} interface. Provides methods for sending
 * synchronous and asynchronous HTTP requests to the M-Pesa API.
 */
public class ClientImpl implements Client {
    private final OkHttpClient httpClient;
    private final Configuration configuration;
    private final Logger logger;

    /**
     * Constructs a new instance of the {@link ClientImpl} class.
     *
     * @param configuration the configuration object containing M-Pesa API
     *                      connection settings
     * @param logger        java's builtin logging framework
     */
    public ClientImpl(Configuration configuration, Logger logger) {
        this.configuration = configuration;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(
                        this.configuration.getConnectionTimeout(),
                        TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(this.configuration.isRetryOnConnectionFailure())
                .addInterceptor(new RetryInterceptor(this.configuration.getMaxRetries()))
                .build();
        this.logger = logger;
    }

    /**
     * Sends a synchronous HTTP request using the provided RequestClient object
     * and returns the server's response.
     *
     * @param <T>           the type of the request body
     * @param requestClient the client object containing request details such as
     *                      URL, method, headers, and body
     * @return the server's response as a Response object
     * @throws IOException       if an input or output exception occurs during the
     *                           request
     * @throws MpesaSdkException if the response is not successful or if the response
     *                           body is null
     */
    public <T> ResponseClient sendSyncRequest(RequestClient<T> requestClient) throws IOException {
        LoggerUtils.logRequest(requestClient.getUrl(),
                requestClient.getMethod(),
                requestClient.getHeaders() != null ? requestClient.getHeaders().toString() : "N/A",
                requestClient.getBody() != null ? requestClient.getBody().toString() : "N/A",
                logger
        );
        Request request = buildRequest(requestClient);

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            LoggerUtils.logResponse(
                    response.code(),
                    responseBody != null ? responseBody.replaceAll("\\s+", " ") : "N/A",
                    response.headers().toMultimap().toString(),
                    logger
            );
            return ResponseClient.builder()
                    .statusCode(response.code())
                    .body(responseBody)
                    .contentType(response.header("Content-Type"))
                    .build();
        }
    }

    /**
     * Sends an asynchronous HTTP request using the provided RequestClient object
     * and registers the given Callback to be executed when the response is
     * received.
     *
     * @param <T>            the type of the request body
     * @param requestClient  the client object containing request details such as
     *                       URL, method, headers, and body
     * @param callbackClient the callback object to be executed when the response is
     *                       received
     * @throws JsonProcessingException if the request body cannot be serialized
     */
    public <T> void sendAsyncRequest(RequestClient<T> requestClient, CallbackClient callbackClient, CallbackErrorClient callbackError) throws
            JsonProcessingException {
        LoggerUtils.logRequest(requestClient.getUrl(),
                requestClient.getMethod(),
                requestClient.getHeaders() != null ? requestClient.getHeaders().toString() : "N/A",
                requestClient.getBody() != null ? requestClient.getBody().toString() : "N/A",
                logger
        );

        Request request = buildRequest(requestClient);
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : null;

                LoggerUtils.logResponse(
                        response.code(),
                        responseBody != null ? responseBody.replaceAll("\\s+", " ") : "N/A",
                        response.headers().toMultimap().toString(),
                        logger
                );

                String contentType = response.header("Content-Type");
                callbackClient.call(ResponseClient.builder()
                        .statusCode(response.code())
                        .body(responseBody)
                        .contentType(contentType != null ? contentType : et.safaricom.constants.MediaType.JSON.getValue())
                        .build());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.severe("Request failed: " + e.getMessage());
                callbackError.call(e);
            }
        });
    }

    /**
     * Builds an HTTP request using the details provided in the RequestClient
     * object.
     *
     * @param <T>           the type of the request body
     * @param requestClient the client object containing request details such as
     *                      URL, method, headers, and body
     * @return a constructed Request object ready to be executed
     */
    private <T> Request buildRequest(RequestClient<T> requestClient) throws JsonProcessingException {
        MediaType mediaType = MediaType.parse(requestClient.getContentType() == null ? "" : requestClient.getContentType().getValue());
        RequestBody body = null;
        if (requestClient.getBody() != null) {
            String stringBody;
            if (requestClient.getContentType().equals(et.safaricom.constants.MediaType.JSON)) {

                ObjectMapper objectMapper = new ObjectMapper();
                stringBody = objectMapper.writeValueAsString(requestClient.getBody());
            } else if (requestClient.getContentType().equals(et.safaricom.constants.MediaType.XML)) {
                XmlMapper xmlMapper = new XmlMapper();
                stringBody = xmlMapper.writeValueAsString(requestClient.getBody());
            } else stringBody = requestClient.getBody().toString();
            body = RequestBody.create(stringBody, mediaType);
        }

        Request.Builder builder = new Request.Builder()
                .url(this.configuration.getBaseUrl() + requestClient.getUrl())
                .method(requestClient.getMethod(), body);

        if (requestClient.getContentType() != null) {
            builder.addHeader("Content-Type", requestClient.getContentType().getValue());
        }

        // Add Authorization header
        if (requestClient.getAuthorization() != null) {
            builder.addHeader("Authorization", requestClient.getAuthorization());
        }

        // Build query parameters
        if (requestClient.getQueryParams() != null) {
            HttpUrl url = HttpUrl.parse(requestClient.getUrl());
            if (url != null) {
                HttpUrl.Builder urlBuilder = url.newBuilder();
                requestClient.getQueryParams().forEach(urlBuilder::addQueryParameter);
                builder.url(urlBuilder.build());
            }
        }

        return builder.build();
    }

}
