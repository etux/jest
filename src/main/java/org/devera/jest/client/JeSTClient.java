package org.devera.jest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.common.base.Preconditions;
import org.devera.jest.client.invocations.JeSTInvocation;
import org.devera.jest.client.invocations.JeSTInvocationFactory;

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
            final I request,
            final NamedParam... params
    ) {
        final JeSTResult<O> result = (JeSTResult<O>) createInvocation(methodName, request, params).invoke();
        if (result.isError()) {
            throw result.getException();
        }
        return result;
    }

    private <I, O> JeSTInvocation<I, O> createInvocation(final String methodName, final I request, final NamedParam... params) {
        return JeSTInvocationFactory.create(jaxrsClient, configuration, clientInstance, methodName, request, params);
    }


}
