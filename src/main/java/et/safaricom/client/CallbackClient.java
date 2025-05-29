package et.safaricom.client;

@FunctionalInterface
public interface CallbackClient {
    void call(ResponseClient responseClient);
}