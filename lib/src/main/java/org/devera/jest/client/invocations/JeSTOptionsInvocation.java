package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

import java.util.Map;

public class JeSTOptionsInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTOptionsInvocation(final Client jaxrsClient,
                          final Configuration configuration,
                          final Object clientInstance,
                          final ReSTOperation operation,
                          final Map<String, Object> pathParams,
                          final I request,
                          final Class<O> returnType)
    {
        super(jaxrsClient, configuration, clientInstance, operation, pathParams, request, returnType);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().build("OPTIONS");
    }
}
