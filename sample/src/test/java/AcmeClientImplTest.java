import java.util.UUID;

import com.acme.client.AcmeClient;
import com.acme.client.AcmeClientImp;
import com.acme.client.GetRequest;
import com.acme.client.GetRequestWithParams;
import com.acme.client.GetRequestWithPathParams;
import com.acme.client.NotFoundException;
import com.acme.client.OkTestResponse;
import com.acme.client.PostRequest;
import com.acme.client.PostRequestWithPathParam;
import com.acme.client.PutRequest;
import com.acme.client.PutRequestWithPathParam;
import com.acme.client.Response;
import com.acme.client.SystemErrorResponse;
import com.acme.client.TestClientConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class AcmeClientImplTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private MockServerClient mockServerClient;

    private TestClientConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new TestClientConfiguration(
                "localhost",
                "http",
                String.valueOf(mockServerRule.getPort()),
                "/"
        );
    }

    @Test
    public void simpleGetOperation_with_existing_resource_should_marshal_appropriate_mapping() throws Exception {

        mockServerClient.when(
                request()
                    .withMethod("GET")
                    .withPath("/")
        ).respond(
                response()
                    .withBody(
                            "{\"message\": \"This works\"}"
                    )
                    .withHeader("Content-Type", "application/json")
                    .withStatusCode(200)
        );

        GetRequest request = new GetRequest();

        final AcmeClient testClientPrototype = getAcmeClientImp();
        Response response = testClientPrototype.simpleGetOperationWithInheritsReSTClientDefaultMappings(request);

        mockServerClient.verify(
                request()
                    .withMethod("GET")
                    .withPath("/")
        );

        assertThat(
                OkTestResponse.class.cast(response).getMessage(),
                is("This works")
        );
    }

    @Test
    public void simpleGetOperation_with_query_params_should_marshal_appropriate_mapping() throws Exception {

        mockServerClient.when(
            request()
                .withMethod("GET")
                .withPath("/")
        ).respond(
            response()
                .withBody(
                    "{\"message\": \"This works\"}"
                )
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200)
        );

        GetRequestWithParams request = new GetRequestWithParams();
        request.setParam("value");

        final AcmeClient testClientPrototype = getAcmeClientImp();
        Response response = testClientPrototype.simpleGetOperationWithQueryParams(request);

        mockServerClient.verify(
            request()
                .withMethod("GET")
                .withPath("/")
                .withQueryStringParameter("param", "value")
        );

        assertThat(
            OkTestResponse.class.cast(response).getMessage(),
            is("This works")
        );
    }

    @Test
    public void simpleGetOperation_with_path_params_should_marshal_appropriate_mapping() throws Exception {

        mockServerClient.when(
            request()
                .withMethod("GET")
                .withPath("/pathValue")
        ).respond(
            response()
                .withBody(
                    "{\"message\": \"This works\"}"
                )
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200)
        );

        GetRequestWithPathParams request = new GetRequestWithPathParams();
        request.setPathParam("pathValue");

        final AcmeClient testClientPrototype = getAcmeClientImp();
        Response response = testClientPrototype.simpleGetOperationWithPathParams(request);

        mockServerClient.verify(
            request()
                .withMethod("GET")
                .withPath("/pathValue")
        );

        assertThat(
            OkTestResponse.class.cast(response).getMessage(),
            is("This works")
        );
    }


    @Test
    public void simpleGetOperation_with_system_error_should_marshal_appropriate_mapping() throws Exception {

        mockServerClient.when(
                request()
                    .withMethod("GET")
                    .withPath("/")
        ).respond(
                response()
                    .withBody(
                            "{\"error\": \"This is an error\"}"
                    )
                    .withHeader("Content-Type", "application/json")
                    .withStatusCode(500)
        );

        GetRequest request = new GetRequest();

        final AcmeClient testClientPrototype = getAcmeClientImp();
        Response response = testClientPrototype.simpleGetOperationWithInheritsReSTClientDefaultMappings(request);

        mockServerClient.verify(
                request()
                    .withMethod("GET")
                    .withPath("/")
        );

        assertThat(
                SystemErrorResponse.class.cast(response).getError(),
                is("This is an error")
        );
    }

    @Test
    public void simplePostOperation_with_default_mappings_should_work_fine() {

        mockServerClient.when(
                request()
                    .withMethod("POST")
                    .withPath("/")
                    .withHeader("Content-Type", "application/json")
        ).respond(
                response()
                    .withHeader("Content-Type", "application/json")
                    .withStatusCode(200)
        );

        PostRequest request = new PostRequest();
        request.setInput("input");

        Response response = getAcmeClientImp().simplePostOperationWithOwnMappings(request);

        assertThat(
                response,
                is(nullValue())
        );

        mockServerClient.verify(
                request()
                    .withMethod("POST")
                    .withPath("/")
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"input\":\"input\"}")
        );
    }

    @Test(expected = NotFoundException.class)
    public void simplePostOperation_with_notFound_should_throw_exception() {

        mockServerClient.when(
                request()
                    .withMethod("POST")
                    .withPath("/")
                    .withHeader("Content-Type", "application/json")
        ).respond(
                response()
                    .withHeader("Content-Type", "application/json")
                    .withStatusCode(404)
                    .withBody("{\"exception\" : \"NotFoundException\"}")
        );

        PostRequest request = new PostRequest();
        request.setInput("input");

        getAcmeClientImp().simplePostOperationWithOwnMappings(request);

        mockServerClient.verify(
                request()
                    .withMethod("POST")
                    .withPath("/")
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"input\":\"input\"}")
        );
    }

    @Test
    public void simplePostOperation_with_pathParam_should_return_ok() {

        mockServerClient.when(
            request()
                .withMethod("POST")
                .withPath("/pathValue")
                .withHeader("Content-Type", "application/json")
        ).respond(
            response()
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200)
        );

        PostRequest request = new PostRequest();
        request.setInput("input");

        getAcmeClientImp().simplePostOperationWithOwnMappingsAndPathParamInSignature(request, "pathValue");

        mockServerClient.verify(
            request()
                .withMethod("POST")
                .withPath("/pathValue")
                .withHeader("Content-Type", "application/json")
                .withBody("{\"input\":\"input\"}")
        );
    }

    @Test
    public void simpleGetOperation_with_differentNameParam_should_return_ok() {
        mockServerClient.when(
            request()
                .withMethod("GET")
                .withPath("/path/pathValue")
        ).respond(
            response()
                .withStatusCode(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\":\"content\"}")
        );

        getAcmeClientImp().simpleGetOperationWithDifferentNamedParam( "pathValue");

        mockServerClient.verify(
            request()
                .withMethod("GET")
                    .withPath("/path/pathValue")
        );
    }

    @Test
    public void simplePostOperation_with_pathParamOnRequest_should_return_ok() {

        mockServerClient.when(
            request()
                .withMethod("POST")
                .withPath("/pathValue")
                .withHeader("Content-Type", "application/json")
        ).respond(
            response()
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200)
        );

        PostRequestWithPathParam request = new PostRequestWithPathParam();
        request.setPathName("pathValue");
        request.setBody("body");

        getAcmeClientImp().simplePostOperationWithOwnMappingsAndPathParamInRequest(request);

        mockServerClient.verify(
            request()
                .withMethod("POST")
                .withPath("/pathValue")
                .withHeader("Content-Type", "application/json")
                .withBody("{\"body\":\"body\"}")
        );
    }

    private AcmeClient getAcmeClientImp() {
        return new AcmeClientImp(configuration);
    }

    @Test
    public void simplePostOperationWithHeaderParam_should_return_ok() {

        final String headerParam = "headerValue";

        mockServerClient.when(
            request()
                .withMethod("POST")
                .withHeader("Content-Type", "application/json")
                .withHeader("headerParam", "headerValue")
        ).respond(
            response()
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200)
        );

        PostRequest request = new PostRequest();
        request.setInput("input");


        getAcmeClientImp().simplePostWithHeaderParam(request, headerParam);

        mockServerClient.verify(
            request()
                .withMethod("POST")
                .withHeader("Content-Type", "application/json")
                .withHeader("headerParam", "headerValue")
                .withBody("{\"input\":\"input\"}")
        );
    }

    @Test
    public void simpleDeleteOperation_should_return_ok() {

        mockServerClient.when(
                request()
                .withMethod("DELETE")
                .withPath("/")
        ).respond(
                response()
                .withStatusCode(200)
        );

        getAcmeClientImp()
                .simpleDeleteOperation();

        mockServerClient.verify(
                request()
                    .withMethod("DELETE")
                    .withPath("/")
        );
    }

    @Test
    public void simplePutOperation_should_return_ok() {

        mockServerClient.when(
                request()
                .withMethod("PUT")
                .withPath("/")
        ).respond(
                response()
                .withStatusCode(200)
        );

        final PutRequest request = new PutRequest();
        request.setBody("body");

        Response response = getAcmeClientImp()
                .simplePutOperationWithOwnMappings(request);

        assertThat(
                response,
                is(nullValue())
        );

        mockServerClient.verify(
                request()
                .withMethod("PUT")
                .withPath("/")
                .withBody("{\"body\":\"body\"}")
        );

    }

    @Test
    public void simplePutOperation_with_pathParam_should_return_ok() {
        UUID identifier = UUID.randomUUID();
        mockServerClient.when(
            request()
                .withMethod("PUT")
                .withPath("/" + identifier.toString())
        ).respond(
            response()
                .withStatusCode(200)
        );

        final PutRequestWithPathParam request = new PutRequestWithPathParam();
        request.setIdentifier(identifier);
        request.setBody("body");

        Response response = getAcmeClientImp()
            .simplePutOperationWithPathParamAndOwnMappings(request);

        assertThat(
            response,
            is(nullValue())
        );

        mockServerClient.verify(
            request()
                .withMethod("PUT")
                .withPath("/" + identifier.toString())
                .withBody("{\"body\":\"body\"}")
        );

    }

    @Test
    public void simplePutOperation_with_pathParam_on_signature_should_return_ok() {
        UUID identifier = UUID.randomUUID();
        mockServerClient.when(
            request()
                .withMethod("PUT")
                .withPath("/" + identifier.toString())
        ).respond(
            response()
                .withStatusCode(200)
        );

        final PutRequest request = new PutRequest();
        request.setBody("body");

        Response response = getAcmeClientImp()
            .simplePutOperationWithPathParamOnSignature(request, identifier.toString());

        assertThat(
            response,
            is(nullValue())
        );

        mockServerClient.verify(
            request()
                .withMethod("PUT")
                .withPath("/" + identifier.toString())
                .withBody("{\"body\":\"body\"}")
        );

    }

    @Test
    public void simpleOptionsOperation_should_return_ok() {

        mockServerClient.when(
                request()
                .withMethod("OPTIONS")
                .withPath("/")
        ).respond(
                response()
                .withStatusCode(200)
        );

        getAcmeClientImp().simpleOptionsOperation();

        mockServerClient.verify(
                request()
                .withMethod("OPTIONS")
                .withPath("/")
        );
    }


}
