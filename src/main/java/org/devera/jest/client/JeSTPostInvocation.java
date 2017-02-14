package org.devera.jest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;

public class JeSTPostInvocation<I,O> extends JeSTInvocation<I, O> {
    public JeSTPostInvocation(Configuration configuration, Object clientInstance, String methodName, I request, Client jaxrsClient) {
        super(configuration, clientInstance, methodName, request, jaxrsClient);
    }

    @Override
    protected JeSTResult<O> invoke() {
        final ReSTOperation operation = getReSTOperation();
        final Invocation invocation = getApplicationWebTarget().request().buildPost(Entity.entity(request, "application/json"));
        return processResponse(
                operation,
                invocation.invoke());
    }
}
