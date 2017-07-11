package com.acme.client;

import org.devera.jest.annotations.Protocol;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTHeaderParam;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.annotations.ReSTPathParam;
import org.devera.jest.annotations.ReSTQueryParam;

import static org.devera.jest.annotations.ReSTOperation.Operations.DELETE;
import static org.devera.jest.annotations.ReSTOperation.Operations.OPTIONS;
import static org.devera.jest.annotations.ReSTOperation.Operations.POST;
import static org.devera.jest.annotations.ReSTOperation.Operations.PUT;

@ReSTClient(
        protocol = Protocol.HTTP,
        contextPath = "/path",
        defaultMappings = {
                @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                @ReSTOperationMapping(statusCode = 500, responseClass = SystemErrorResponse.class),
        }
)
public interface AcmeClient {

    @ReSTOperation
    Response simpleGetOperationWithInheritsReSTClientDefaultMappings(GetRequest request);

    @ReSTOperation
    Response simpleGetOperationWithQueryParams(GetRequestWithParams request);

    @ReSTOperation(
        path = "/{pathParam}"
    )
    Response simpleGetOperationWithPathParams(GetRequestWithPathParams request);

    @ReSTOperation(
            path = "/",
            method = POST,
            mappings = {
                    @ReSTOperationMapping(statusCode = 200),
                    @ReSTOperationMapping(statusCode = 404, exceptionClass = NotFoundException.class)
            }
    )
    OkTestResponse simplePostOperationWithOwnMappings(PostRequest request);

    @ReSTOperation(
        path = "/{pathName}",
        method = POST,
        mappings = {
            @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
            @ReSTOperationMapping(statusCode = 404, exceptionClass = NotFoundException.class)
        }
    )
    Response simplePostOperationWithOwnMappingsAndPathParamInSignature(PostRequest request, @ReSTPathParam String pathName);


    @ReSTOperation(
        path = "/{pathName}",
        method = POST,
        mappings = {
            @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
            @ReSTOperationMapping(statusCode = 404, exceptionClass = NotFoundException.class)
        }
    )
    Response simplePostOperationWithOwnMappingsAndPathParamInRequest(PostRequestWithPathParam request);

    @ReSTOperation(
            method = POST
    )
    Response simplePostWithHeaderParam(PostRequest request, @ReSTHeaderParam String headerParam);

    @ReSTOperation(
            method = PUT,
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                    @ReSTOperationMapping(statusCode = 409, responseClass = ConflictResponse.class)
            }
    )
    Response simplePutOperationWithOwnMappings(PutRequest request);

    @ReSTOperation(
        method = PUT,
        path = "/{identifier}",
        mappings = {
            @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
            @ReSTOperationMapping(statusCode = 409, responseClass = ConflictResponse.class)
        }
    )
    Response simplePutOperationWithPathParamAndOwnMappings(PutRequestWithPathParam request);

    @ReSTOperation(
        method = PUT,
        path="/{identifier}"
    )
    Response simplePutOperationWithPathParamOnSignature(PutRequest request, @ReSTPathParam String identifier);

    @ReSTOperation(
            path="/path/{param}"
    )
    Response simpleGetOperationWithPathParamAndHeaderParam(@ReSTPathParam String pathParam, @ReSTQueryParam String queryParam, @ReSTHeaderParam String headerParam);

    @ReSTOperation(
            path="/path/{differentParam}",
            mappings = {
                    @ReSTOperationMapping(statusCode = 200)
            }
    )
    OkTestResponse simpleGetOperationWithDifferentNamedParam(@ReSTPathParam("differentParam") String pathParam);

    @ReSTOperation(
            method = DELETE
    )
    Response simpleDeleteOperation();

    @ReSTOperation(
            method = OPTIONS
    )
    Response simpleOptionsOperation();
}
