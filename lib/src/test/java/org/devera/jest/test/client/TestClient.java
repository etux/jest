package org.devera.jest.test.client;

import java.util.Map;

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
public interface TestClient {

    @ReSTOperation
    Response simpleGetOperationWithInheritsReSTClientDefaultMappings(GetRequest request);

    @ReSTOperation
    Response simpleGetOperationWithQueryParams(GetRequestWithParams request);

    @ReSTOperation
    Response simpleGetOperationWithQueryParams(GetRequestWithParams request, @ReSTQueryParam String param);

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
            method = DELETE
    )
    Response simpleDeleteOperation();

    @ReSTOperation(
            method = OPTIONS
    )
    Response simpleOptionsOperation();

    @ReSTOperation(
            method = POST
    )
    Response simplePostWithHeaderParam(PostRequest request, @ReSTHeaderParam String headerParam);

    @ReSTOperation(
            method = POST
    )
    Response simplePostWithHeaderParamNameOverwriting(PostRequest request, @ReSTHeaderParam("anotherHeaderParam") String headerParam);

    @ReSTOperation(
            method = POST
    )
    Response simplePostWithHeaderParams(PostRequest request, @ReSTHeaderParam Map<String, String> headerParams);
}
