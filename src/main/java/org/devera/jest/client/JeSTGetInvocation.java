package org.devera.jest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;

import org.devera.jest.annotations.ReSTOperation;

public class JeSTGetInvocation<I,O> extends JeSTInvocation<I,O> {

    public JeSTGetInvocation(Configuration configuration, Object clientInstance, String methodName, I request, Client jaxrsClient) {
        super(configuration, clientInstance, methodName, request, jaxrsClient);
    }

    @Override
    protected JeSTResult<O> invoke() {
        {
            final ReSTOperation operation = getReSTOperation();
            final Invocation invocation = getApplicationWebTarget()
                       .request()
                       .build(operation.method());
               return processResponse(operation, invocation.invoke());
           }
    }

}
