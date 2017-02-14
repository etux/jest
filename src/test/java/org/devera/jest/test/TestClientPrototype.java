package org.devera.jest.test;

import org.devera.jest.annotations.Response;
import org.devera.jest.client.JeSTClient;
import org.devera.jest.client.JeSTResult;

public class TestClientPrototype implements TestClient {

    private final TestClientConfiguration configuration;
    private final JeSTClient jeSTClient;

    public TestClientPrototype(final TestClientConfiguration configuration) {

        jeSTClient = new JeSTClient(configuration, this);
        this.configuration = configuration;

    }

    @Override
    public Response simpleGetOperationWithInheritsReSTClientDefaultMappings(GetRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithInheritsReSTClientDefaultMappings", request);
        return result.getPayload();
    }

    @Override
    public Response simplePostOperationWithOwnMappings(PostRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostOperationWithOwnMappings", request);
        return result.getPayload();
    }

    @Override
    public Response operation2() {
        return null;
    }

    @Override
    public Response operation3() {
        return null;
    }
}
