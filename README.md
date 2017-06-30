# jest

The purpose of this project is to enable java developers to create ReST clients by defining an interface and let this project generate the necesary plumbing to actually invoke a ReST Web Service.

It should look something like this:
```
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
            method = DELETE
    )
    Response simpleDeleteOperation();

    @ReSTOperation(
            method = OPTIONS
    )
    Response simpleOptionsOperation();
}
```

Initially, the approach to follow is to use annotation processors to generate the specific code that together with a library that uses reflexion allows the developer to easily introduce ReST Clients on his applications.

## Query parameters
Currently the implementation allows the use of query parameters in the form of method parameters as well as request attributes, in this case the use of @JeSTQueryParam is optional.

In this example we see how parameters from a request object end up being used as query parameters.
```
package com.acme.client;

import java.util.List;

public class GetRequestWithParams
{
    private String param;

    private List<String> listParam;

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }

    public List<String> getListParam()
    {
        return listParam;
    }

    public void setListParam(List<String> listParam)
    {
        this.listParam = listParam;
    }
}
```

## Path parameters
Currently the implementation allows the use of path parameters in the form of method parameters as well as request attributes with @JeSTPathParam annotations.

Example of how to use it in a request object.
```
package com.acme.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.devera.jest.annotations.ReSTPathParam;

public class PostRequestWithPathParam
{
    @ReSTPathParam
    @JsonIgnore
    private String pathName;

    private String body;

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName(String pathName)
    {
        this.pathName = pathName;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
```
Note that in case the parameter should not be part of the JSON representation, a @JsonIgnore annotation needs to be used.

## Header parameters
Currently the implementation allows the use of header parameters in the form method parameters as well as request attributes with @JeSTHeaderParam annotations.

At the moment the implementation is focused on using Jersey Client 2.x as the web services stack, and it requires Java 8.
