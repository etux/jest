package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.client.Configuration;

public class JeSTOptionsInvocation<I,O> extends JeSTInvocation<I, O> {

    JeSTOptionsInvocation(final Client jaxrsClient,
                          final Configuration configuration,
                          final JeSTInvocationHelper invocationHelper)
    {
        super(jaxrsClient, configuration, invocationHelper);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().build("OPTIONS");
    }
}
