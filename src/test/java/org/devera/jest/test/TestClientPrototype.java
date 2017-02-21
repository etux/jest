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
    public Response simpleGetOperationWithQueryParams(GetRequestWithParams request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithQueryParams", request);
        return result.getPayload();
    }

    @Override
    public Response simpleGetOperationWithPathParams(GetRequestWithPathParams request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithPathParams", request);
        return result.getPayload();
    }

    @Override
    public Response simplePostOperationWithOwnMappings(PostRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostOperationWithOwnMappings", request);
        return result.getPayload();
    }

    @Override
    public Response simplePutOperationWithOwnMappings(PutRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePutOperationWithOwnMappings", request);
        return result.getPayload();
    }

    @Override
    public Response simplePutOperationWithPathParamAndOwnMappings(PutRequestWithPathParam request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePutOperationWithPathParamAndOwnMappings", request);
        return result.getPayload();
    }

    @Override
    public Response simpleDeleteOperation() {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleDeleteOperation", null);
        return result.getPayload();
    }

    @Override
    public Response simpleOptionsOperation() {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleOptionsOperation", null);
        return result.getPayload();
    }
}
