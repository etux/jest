package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

public final class JeSTPostInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTPostInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final I request,
            final Class<O> returnType)
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, request, returnType);
    }

    @Override
    protected final Invocation prepareInvocation() {
        return resolveWebTarget()
                .request()
                .buildPost(Entity.entity(request, MediaType.APPLICATION_JSON));
    }
}
