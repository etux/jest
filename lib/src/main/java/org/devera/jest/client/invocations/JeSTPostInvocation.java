package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

import java.util.Map;

public final class JeSTPostInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTPostInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final Map<String, Object> pathParams,
            final I request,
            final Class<O> returnType)
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, pathParams, request, returnType);
    }

    @Override
    protected final Invocation prepareInvocation() {
        return resolveWebTarget()
                .request()
                .buildPost(Entity.entity(request, MediaType.APPLICATION_JSON));
    }
}
