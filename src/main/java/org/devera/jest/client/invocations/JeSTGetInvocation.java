package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

public class JeSTGetInvocation<I,O> extends JeSTInvocation<I,O> {

    JeSTGetInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final I request,
            final Class<O> responseClass
    )
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, request, responseClass);
    }

    @Override
    protected final Invocation prepareInvocation() {
        return
            resolveWebTarget()
                .request()
                .buildGet();
    }
}
