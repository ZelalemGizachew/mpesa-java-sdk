package et.safaricom.client;

@FunctionalInterface
public interface CallbackErrorClient {
    void call(Exception exception);
}