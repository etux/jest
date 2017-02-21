# jest

The purpose of this project is to enable java developers to create ReST clients by defining an interface and let this project generate the necesary plumbing to actually invoke a ReST Web Service.

It should look something like this:
```
package org.devera.jest.test;

import org.devera.jest.annotations.Protocol;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.annotations.Response;

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

    @ReSTOperation(
        path = "/{pathParam}"
    )
    Response simpleGetOperationWithPathParams(GetRequestWithPathParams request);

    @ReSTOperation(
            path = "/",
            method = "POST",
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = OkTestResponse.class),
                    @ReSTOperationMapping(statusCode = 404, responseClass = NotFoundResponse.class)
            }
    )
    Response simplePostOperationWithOwnMappings(PostRequest request);

    @ReSTOperation(
            method = "DELETE",
            mappings = {
                    @ReSTOperationMapping(statusCode = 200, responseClass = Void.class),
                    @ReSTOperationMapping(statusCode = 404, responseClass = NotFoundResponse.class)
            }
    )
    Response simpleDeleteOperation();
}
```

Initially, the approach to follow is to use annotation processors to generate the specific code that together with a library that uses reflexion allows the developer to easily introduce ReST Clients on his applications.

## Query parameters
Currently the implementation allows the use of query parameters in the form of properties of the request object by the use of reflection for get operations. The use of @JeSTQueryParam is optional, in this case.

## Path parameters
Currently the implementation allows the use of path parameters in the form of properties of the request object by the use of reflection together with @JeSTPathParam annotations.

At the moment the implementation is focused on using Jersey Client 2.x as the web services stack, and it requires Java 8.
