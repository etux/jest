package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import org.devera.jest.client.Configuration;

public final class JeSTPostInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTPostInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final JeSTInvocationHelper jeSTInvocationHelper)
    {
        super(jaxrsClient, configuration, jeSTInvocationHelper);
    }

    @Override
    protected final Invocation prepareInvocation() {
        return resolveWebTarget()
                .request()
                .buildPost(Entity.entity(getRequest(), MediaType.APPLICATION_JSON));
    }
}
