package et.safaricom.utils;

import java.util.logging.*;

/**
 * Utility class for creating and configuring {@link Logger} instances.
 *
 * <p>This class includes methods for creating a logger with a configured console
 * handler and formatting log messages with a timestamp, level, logger name, and
 * message.
 */
public class LoggerUtils {
    /**
     * Creates and configures a {@link Logger} instance for the provided class and logging level.
     *
     * @param clazz the class for which the logger is being created. This value is used
     *              to set the logger's name to the fully qualified class name.
     * @param level the logging level to be applied to the logger.
     * @return a configured {@link Logger} instance with a console handler that formats
     *         log messages with a timestamp, level, logger name, and message.
     */
    public static Logger createLogger(Class<?> clazz, Level level) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setLevel(level);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(
                        "[%1$tF %1$tT] [%2$-7s] [%3$s] %4$s %n",
                        record.getMillis(),
                        record.getLevel(),
                        record.getLoggerName(),
                        record.getMessage()
                );
            }
        });

        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        return logger;
    }


    /**
     * Logs the details of an HTTP request, including the URL, method, headers, and body,
     * using the provided logger instance.
     *
     * @param url     the URL of the HTTP request
     * @param method  the HTTP method used for the request (e.g., GET, POST)
     * @param headers the headers included in the HTTP request
     * @param body    the body of the HTTP request; null if the body does not exist
     * @param logger  the logger instance used to record the request details
     */
    public static void logRequest(String url, String method, String headers, String body, Logger logger) {
        logger.info(String.format(
                "HTTP Request = URL: %s - Method: %s - Headers: %s - Body: %s",
                url, method, headers, body != null ? body : "N/A"
        ));
    }

    /**
     * Logs the details of an HTTP response, including the status code, headers,
     * and body, using the provided logger instance.
     *
     * @param statusCode the HTTP status code of the response
     * @param headers    the headers returned in the HTTP response as a string
     * @param body       the body of the HTTP response; null if the body does not exist
     * @param logger     the logger instance used to record the response details
     */
    public static void logResponse(int statusCode, String headers, String body, Logger logger) {
        logger.info(String.format(
                "HTTP Response = Status: %d - Body: %s - Headers: %s",
                statusCode, headers, body != null ? body : "N/A"
        ));
    }
}
