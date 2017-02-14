package org.devera.jest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTOperation;

public class JeSTClient {

    private final Configuration configuration;
    private final Client jaxrsClient;
    private final Object clientInstance;

    public JeSTClient(final Configuration configuration,
                      final Object client) {
        Preconditions.checkNotNull(client);
        Preconditions.checkNotNull(configuration);

        this.configuration = configuration;
        this.clientInstance = client;
        this.jaxrsClient = ClientBuilder.newBuilder().build();
    }

    public <I, O> JeSTResult<O> invoke(
            final String methodName,
            final I request
    ) {
        return (JeSTResult<O>) createInvocation(methodName, request).invoke();
    }

    private <I, O> JeSTInvocation<I, O> createInvocation(final String methodName, final I request) {
        return JeSTInvocationFactory.create(methodName, request, configuration, clientInstance, jaxrsClient);
    }

    private static class JeSTInvocationFactory {
        private static <I, O> JeSTInvocation<I, O> create(
                String methodName,
                I request,
                Configuration configuration,
                Object clientInstance,
                Client jaxrsClient) {

            ReSTOperation operation = ReflectionUtils.findReSTOperation(clientInstance, methodName, request.getClass());

            switch(operation.method()) {
                case GET:
                    return new JeSTGetInvocation<>(
                            configuration,
                            clientInstance,
                            methodName,
                            request,
                            jaxrsClient
                    );
                case POST:
                    return new JeSTPostInvocation<>(
                            configuration,
                            clientInstance,
                            methodName,
                            request,
                            jaxrsClient
                    );
                default:
                    throw new IllegalArgumentException("Method " + operation.method() + " not supported.");
            }
        }
    }


}
