package org.devera.jest.test;

import org.devera.jest.annotations.Request;
import org.devera.jest.annotations.Response;
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

    @Test
    public void simpleGetOperation() throws Exception {

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

        Request request = new Request() {};

        TestClientConfiguration configuration = new TestClientConfiguration(
                "localhost",
                "http",
                String.valueOf(mockServerRule.getPort()),
                "/"
        );

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

}