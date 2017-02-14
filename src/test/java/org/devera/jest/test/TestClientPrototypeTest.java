package org.devera.jest.test;

import org.devera.jest.annotations.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class TestClientPrototypeTest {

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

        final TestClientPrototype testClientPrototype = new TestClientPrototype(configuration);
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

        final TestClientPrototype testClientPrototype = new TestClientPrototype(configuration);
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

        Response response = new TestClientPrototype(configuration).simplePostOperationWithOwnMappings(request);

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
        );

        PostRequest request = new PostRequest();
        request.setInput("input");

        Response response = new TestClientPrototype(configuration).simplePostOperationWithOwnMappings(request);

        mockServerClient.verify(
                request()
                    .withMethod("POST")
                    .withPath("/")
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"input\":\"input\"}")
        );
    }

}