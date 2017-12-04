package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.client.Configuration;

public class JeSTDeleteInvocation<I,O> extends JeSTInvocation<I,O> {

    JeSTDeleteInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final JeSTInvocationHelper<I,O> invocationHelper) {
        super(jaxrsClient, configuration, invocationHelper);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().buildDelete();
    }
}
