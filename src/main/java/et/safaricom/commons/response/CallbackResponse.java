package et.safaricom.commons.response;

@FunctionalInterface
public interface CallbackResponse<T> {
    void call(T response);
}
