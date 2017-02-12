package org.devera.jest.client;

public class JeSTResult<T> {

    private final Object resultPayload;
    private final Class<T> responseClass;

    public JeSTResult(
            final Class<T> responseClass,
            final Object resultPayload)
    {
        this.resultPayload = resultPayload;
        this.responseClass = responseClass;
    }

    public T getPayload() {
        return responseClass.cast(resultPayload);
    }

}
