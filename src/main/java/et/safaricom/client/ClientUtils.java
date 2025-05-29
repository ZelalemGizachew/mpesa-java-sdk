package et.safaricom.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import et.safaricom.auth.Authorization;
import et.safaricom.commons.response.CallbackError;
import et.safaricom.commons.response.CallbackResponse;
import et.safaricom.commons.response.MpesaError;
import et.safaricom.constants.MediaType;
import et.safaricom.exceptions.MpesaApiException;
import et.safaricom.exceptions.MpesaSdkException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
@AllArgsConstructor
public class ClientUtils {
    private final Client client;
    private final Authorization authorization;
    private final Logger log;
    private ObjectMapper objectMapper = new ObjectMapper();
    private XmlMapper xmlMapper = new XmlMapper();

    /**
     * Sends an asynchronous request to the specified endpoint and processes the
     * response into a generic response object. The {@code callbackResponse} is
     * used to handle the response asynchronously, while the {@code callbackError}
     * is used to handle any errors that might occur during request processing.
     *
     * @param endpoint         the URL endpoint to which the request is sent.
     * @param request          the request payload to be sent to the M-Pesa API.
     * @param responseType     the class type of the response object.
     * @param callbackResponse the callback method to be executed when the response
     *                         is
     *                         received.
     * @param callbackError    the callback method to be executed if an error occurs
     *                         during request processing.
     * @param <T>              the type of the response object.
     */
    public <T, R> void processASyncRequest(String endpoint, R request, Class<T> responseType,
                                           CallbackResponse<T> callbackResponse, CallbackError callbackError) {
        processASyncRequest(endpoint, request, Map.of(), MediaType.JSON, responseType, callbackResponse, callbackError);
    }

    /**
     * Sends an asynchronous request to the specified endpoint and processes the
     * response into a generic response object. The {@code callbackResponse} is
     * used to handle the response asynchronously, while the {@code callbackError}
     * is used to handle any errors that might occur during request processing.
     *
     * @param endpoint         the URL endpoint to which the request is sent.
     * @param request          the request payload to be sent to the M-Pesa API.
     * @param responseType     the class type of the response object.
     * @param callbackResponse the callback method to be executed when the response
     *                         is
     *                         received.
     * @param callbackError    the callback method to be executed if an error occurs
     *                         during request processing.
     * @param <T>              the type of the response object.
     */
    public <T, R> void processASyncRequest(String endpoint, R request, Map<String, String> query, MediaType contentType, Class<T> responseType,
                                           CallbackResponse<T> callbackResponse, CallbackError callbackError) {
        RequestClient<R> requestClient = createRequestClient(endpoint, request, contentType, query);
        try {
            client.sendAsyncRequest(requestClient,
                    response -> handleCallback(response, callbackResponse, callbackError, responseType),
                    callbackError::call);
        } catch (Exception e) {
            callbackError.call(new MpesaSdkException(e.getMessage(), e));
        }
    }

    /**
     * Creates a RequestClient object for sending requests to the specified
     * endpoint.
     * The request object is wrapped within the RequestClient for transmission.
     *
     * @param endpoint the URL endpoint to which the request is sent.
     * @param request  the request payload to be sent to the M-Pesa API.
     * @param <T>      the type of the request body.
     * @return a configured RequestClient object.
     */
    public <T> RequestClient<T> createRequestClient(String endpoint, T request) {
        return createRequestClient(endpoint, request, MediaType.JSON, Map.of());
    }

    /**
     * Creates a RequestClient object for sending requests to the specified
     * endpoint.
     * The request object is wrapped within the RequestClient for transmission.
     *
     * @param endpoint the URL endpoint to which the request is sent.
     * @param request  the request payload to be sent to the M-Pesa API.
     * @param <T>      the type of the request body.
     * @return a configured RequestClient object.
     */
    private <T> RequestClient<T> createRequestClient(String endpoint, T request, MediaType contentType, Map<String, String> queryParams) {
        return RequestClient.<T>builder()
                .url(endpoint)
                .method("POST")
                .authorization(authorization.getAccessToken())
                .contentType(contentType)
                .queryParams(queryParams)
                .body(request)
                .build();
    }

    /**
     * Handles the callback response from the M-Pesa API. It parses the response
     * body
     * into the specified response type and invokes the appropriate callback
     * functions based on the response status code.
     *
     * @param responseClient   the response object from the M-Pesa API.
     * @param callbackResponse the callback to be executed with the parsed response
     *                         object.
     * @param callbackError    the callback to be executed if an error occurs during
     *                         processing.
     * @param responseType     the class type of the expected response object.
     * @param <T>              the type of the response object.
     * @throws MpesaSdkException if the response body is null or if any exception
     *                           occurs during processing.
     */
    private <T> void handleCallback(ResponseClient responseClient, CallbackResponse<T> callbackResponse,
                                    CallbackError callbackError, Class<T> responseType) {
        try {

            if (responseClient.getBody() == null) {
                throw new MpesaSdkException("Response body is null");
            }

            String responseBody = responseClient.getBody();
            if (responseClient.getStatusCode() % 400 < 100) {
                MpesaError mpesaError = objectMapper.readValue(responseBody, MpesaError.class);
                log.severe("Encountered client error: " + mpesaError.getErrorMessage());
                callbackError.call(new MpesaApiException(mpesaError.getErrorMessage(), mpesaError));
                return;
            }

            // specific to validate and confirm endpoints
            // TODO: make this more generic
            if (responseClient.getContentType().equals(MediaType.XML.getValue())) {
                JsonNode rootNode = xmlMapper.readTree(responseBody);
                callbackResponse.call(xmlMapper.treeToValue(
                        rootNode.path("Body").path("C2BPaymentValidationResult"), // Navigate to the inner element (skipping the parent tags)
                        responseType
                ));
            } else {
                callbackResponse.call(objectMapper.readValue(responseBody, responseType));
            }
        } catch (IOException e) {
            log.severe("Encountered error: " + e.getMessage());
            callbackError.call(new MpesaSdkException(e.getMessage(), e));
        }
    }

    /**
     * Sends a synchronous request to the specified endpoint and processes the
     * response into a generic response object.
     *
     * @param endpoint     the URL endpoint to which the request is sent.
     * @param request      the request payload to be sent to the M-Pesa API.
     * @param responseType the class type of the response object.
     * @param <S>          the type of the response object.
     * @return the parsed response object of type T.
     * @throws MpesaSdkException if the response body is null or an error occurs
     *                           during
     *                           request processing.
     */
    public <R, S> S processSyncRequest(String endpoint, R request, Class<S> responseType)
            throws IOException, MpesaApiException {
        RequestClient<R> requestClient = createRequestClient(endpoint, request);
        ResponseClient response = client.sendSyncRequest(requestClient);
        return parseResponse(response, responseType);
    }

    /**
     * Parses the response body from the M-Pesa API response into a generic object
     * of type T.
     * If the response status code is not 200, an MpesaApiException is thrown with
     * the error
     * message and MpesaError object from the response body.
     *
     * @param response     the response object from the M-Pesa API.
     * @param responseType the class type of the response object.
     * @param <T>          the type of the response object.
     * @return the parsed response object of type T.
     * @throws MpesaApiException if the response status code is not 200.
     * @throws IOException       if an error occurs during request processing.
     */
    private <T> T parseResponse(ResponseClient response, Class<T> responseType) throws IOException, MpesaApiException {
        if (response.getBody() == null) {
            throw new MpesaSdkException("Response body is null");
        }

        String responseBody = response.getBody();
        if (response.getStatusCode() != 200) {
            MpesaError mpesaError = objectMapper.readValue(responseBody, MpesaError.class);
            throw new MpesaApiException(mpesaError.getErrorMessage(), mpesaError);
        }

        return objectMapper.readValue(responseBody, responseType);
    }
}
