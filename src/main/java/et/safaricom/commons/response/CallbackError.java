package et.safaricom.commons.response;

@FunctionalInterface
public interface CallbackError {
    void call(Exception exception);
}
