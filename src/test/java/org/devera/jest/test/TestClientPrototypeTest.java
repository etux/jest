package org.devera.jest.test;

import org.devera.jest.annotations.Request;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

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
                    .withStatusCode(200)
                    .withBody("")
        );

        Request request = new Request() {};

        TestClientConfiguration configuration = new TestClientConfiguration(
                "localhost",
                "http",
                String.valueOf(mockServerRule.getPort()),
                "/"
        );

        final TestClientPrototype testClientPrototype = new TestClientPrototype(configuration);
        testClientPrototype.simpleGetOperation(request);
    }

}