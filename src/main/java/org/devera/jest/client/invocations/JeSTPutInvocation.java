package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;

public class JeSTPutInvocation<I, O> extends JeSTInvocation<I, O> {

    public JeSTPutInvocation(Client jaxrsClient,
                             Configuration configuration,
                             Object clientInstance,
                             ReSTOperation reSTOperation,
                             I request)
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, request);
    }

    @Override
    protected Invocation prepareInvocation() {
        return getApplicationWebTarget().request().buildPut(Entity.entity(request, MediaType.APPLICATION_JSON));
    }
}
