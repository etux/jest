package org.devera.jest.test;

import org.devera.jest.annotations.Protocol;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.annotations.Response;

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

    @ReSTOperation(
        path = "/{pathParam}"
    )
    Response simpleGetOperationWithPathParams(GetRequestWithPathParams request);

    @ReSTOperation(
            path = "/",
            method = POST,
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                    @ReSTOperationMapping(statusCode = 404, exceptionClass = NotFoundException.class)
            }
    )
    Response simplePostOperationWithOwnMappings(PostRequest request);

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
            method = DELETE
    )
    Response simpleDeleteOperation();

    @ReSTOperation(
            method = OPTIONS
    )
    Response simpleOptionsOperation();
}
