package org.devera.jest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.common.base.Preconditions;
import org.devera.jest.client.invocations.JeSTInvocation;
import org.devera.jest.client.invocations.JeSTInvocationFactory;
import org.devera.jest.client.invocations.JeSTInvocationHelper;
import org.devera.jest.client.params.NamedParam;

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
            final Class<O> responseClass,
            final NamedParam... generatedNamedParams
    ) {
        final JeSTResult<O> result = createInvocation(
                new JeSTInvocationHelper(
                        clientInstance,
                        methodName,
                        request,
                        responseClass,
                        generatedNamedParams)
        )
                .invoke();
        if (result.isError()) {
            throw result.getException();
        }
        return result;
    }

    private <I, O> JeSTInvocation<I, O> createInvocation(final JeSTInvocationHelper<I,O> invocationHelper) {
        return JeSTInvocationFactory.create(
                jaxrsClient,
                configuration,
                invocationHelper
        );
    }


}
