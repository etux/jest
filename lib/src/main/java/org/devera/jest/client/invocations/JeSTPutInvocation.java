package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import org.devera.jest.client.Configuration;

public class JeSTPutInvocation<I, O> extends JeSTInvocation<I, O>
{
    JeSTPutInvocation(final Client jaxrsClient,
                      final Configuration configuration,
                      final JeSTInvocationHelper invocationHelper)
    {
        super(jaxrsClient, configuration, invocationHelper);
    }

    @Override
    protected final Invocation prepareInvocation()
    {
        return resolveWebTarget().request().buildPut(Entity.entity(getRequest(), MediaType.APPLICATION_JSON));
    }
}
