package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

public class JeSTDeleteInvocation<I,O> extends JeSTInvocation<I,O> {

    JeSTDeleteInvocation(
            Client jaxrsClient,
            Configuration configuration,
            Object clientInstance,
            ReSTOperation reSTOperation,
            I request
    ) {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, request);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().buildDelete();
    }
}
