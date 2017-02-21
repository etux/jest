package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

import java.util.Map;

public class JeSTDeleteInvocation<I,O> extends JeSTInvocation<I,O> {

    JeSTDeleteInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final Map<String, Object> pathParams,
            final I request,
            final Class<O> returnType) {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, pathParams, request, returnType);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().buildDelete();
    }
}
