package org.devera.jest.test.client;

import java.util.Map;

import org.devera.jest.annotations.ReSTQueryParam;
import org.devera.jest.client.params.HeaderParam;
import org.devera.jest.client.JeSTClient;
import org.devera.jest.client.JeSTResult;
import org.devera.jest.client.params.PathParam;
import org.devera.jest.client.params.QueryParam;

public class TestClientPrototype implements TestClient {

    private final TestClientConfiguration configuration;
    private final JeSTClient jeSTClient;

    public TestClientPrototype(final TestClientConfiguration configuration) {
        jeSTClient = new JeSTClient(configuration, this);
        this.configuration = configuration;
    }

    @Override
    public Response simpleGetOperationWithInheritsReSTClientDefaultMappings(GetRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithInheritsReSTClientDefaultMappings", request, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simpleGetOperationWithQueryParams(GetRequestWithParams request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithQueryParams", request, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simpleGetOperationWithQueryParams(GetRequestWithParams request, @ReSTQueryParam String param) {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithQueryParams", request, Response.class, new QueryParam("param", param));
        return result.getPayload();
    }

    @Override
    public Response simpleGetOperationWithPathParams(GetRequestWithPathParams request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleGetOperationWithPathParams", request, Response.class);
        return result.getPayload();
    }

    @Override
    public OkTestResponse simplePostOperationWithOwnMappings(PostRequest request) {
        final JeSTResult<OkTestResponse> result = jeSTClient.invoke("simplePostOperationWithOwnMappings", request, OkTestResponse.class);
        return result.getPayload();
    }

    @Override
    public Response simplePostOperationWithOwnMappingsAndPathParamInSignature(PostRequest request, String pathName)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostOperationWithOwnMappingsAndPathParamInSignature", request, Response.class, new PathParam("pathName", pathName));
        return result.getPayload();
    }

    @Override
    public Response simplePostOperationWithOwnMappingsAndPathParamInRequest(PostRequestWithPathParam request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostOperationWithOwnMappingsAndPathParamInRequest", request, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simplePostWithHeaderParam(PostRequest request, String headerParam) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostWithHeaderParam", request, Response.class, new HeaderParam("headerParam", headerParam));
        return result.getPayload();
    }

    @Override
    public Response simplePostWithHeaderParamNameOverwriting(PostRequest request, String headerParam) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePostWithHeaderParamNameOverwriting", request, Response.class, new HeaderParam("anotherHeaderParam", headerParam));
        return result.getPayload();
    }

    @Override
    public Response simplePostWithHeaderParams(PostRequest request, Map<String, String> headerParams) {
        return null;
    }

    @Override
    public Response simplePutOperationWithOwnMappings(PutRequest request) {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePutOperationWithOwnMappings", request, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simplePutOperationWithPathParamAndOwnMappings(PutRequestWithPathParam request)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePutOperationWithPathParamAndOwnMappings", request, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simplePutOperationWithPathParamOnSignature(PutRequest request, String identifier)
    {
        final JeSTResult<Response> result = jeSTClient.invoke("simplePutOperationWithPathParamOnSignature",
            request,
            Response.class,
            new PathParam("identifier", identifier));

        return result.getPayload();
    }

    @Override
    public Response simpleDeleteOperation() {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleDeleteOperation", null, Response.class);
        return result.getPayload();
    }

    @Override
    public Response simpleOptionsOperation() {
        final JeSTResult<Response> result = jeSTClient.invoke("simpleOptionsOperation", null, Response.class);
        return result.getPayload();
    }
}
