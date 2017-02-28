package org.devera.jest.client.invocations;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.util.Map;

public class JeSTPutInvocation<I, O> extends JeSTInvocation<I, O>
{
    JeSTPutInvocation(final Client jaxrsClient,
                      final Configuration configuration,
                      final Object clientInstance,
                      final ReSTOperation reSTOperation,
                      final Map<String, Object> pathParams,
                      final I request,
                      final Class<O> responseClass)
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, pathParams, request, responseClass);
    }

    @Override
    protected final Invocation prepareInvocation()
    {
        return resolveWebTarget().request().buildPut(Entity.entity(request, MediaType.APPLICATION_JSON));
    }
}
