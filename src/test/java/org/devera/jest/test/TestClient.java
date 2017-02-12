package org.devera.jest.test;

import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.annotations.Protocol;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.Request;
import org.devera.jest.annotations.Response;

@ReSTClient(
        protocol = Protocol.HTTP,
        contextPath = "/path",
        defaultMappings = {
                @ReSTOperationMapping(statusCode = 500, responseClass = SystemErrorResponse.class)
        }
)
public interface TestClient {

    @ReSTOperation
    Response simpleGetOperation(Request request);


    @ReSTOperation(
            path = "/",
            method = "POST",
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                    @ReSTOperationMapping(statusCode = 409, responseClass = NotFoundResponse.class)
            }
    )
    Response operation1();

    @ReSTOperation(
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                    @ReSTOperationMapping(statusCode = 403, responseClass = Response.class)
            }
    )
    Response operation2();


    @ReSTOperation
    Response operation3();
}
