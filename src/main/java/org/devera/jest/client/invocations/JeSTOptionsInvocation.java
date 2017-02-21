package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

public class JeSTOptionsInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTOptionsInvocation(final Client jaxrsClient,
                          final Configuration configuration,
                          final Object clientInstance,
                          final ReSTOperation operation,
                          final I request,
                          final Class<O> returnType)
    {
        super(jaxrsClient, configuration, clientInstance, operation, request, returnType);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().build("OPTIONS");
    }
}