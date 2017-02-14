package org.devera.jest.client;

import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

import static org.devera.jest.client.ReflectionUtils.findOperationMapping;
import static org.devera.jest.client.ReflectionUtils.findReSTOperation;

public abstract class JeSTInvocation<I, O> {

    protected final Configuration configuration;
    protected final Object clientInstance;
    protected final String methodName;
    protected final I request;
    private final Client jaxrsClient;

    public JeSTInvocation(
            final Configuration configuration,
            final Object clientInstance,
            final String methodName,
            final I request,
            final Client jaxrsClient
    ) {
        this.configuration = configuration;
        this.clientInstance = clientInstance;
        this.methodName = methodName;
        this.request = request;
        this.jaxrsClient = jaxrsClient;
    }


    protected abstract JeSTResult<O> invoke();

    protected final WebTarget getApplicationWebTarget() {
        return jaxrsClient.target(configuration.getApplicationUrl(clientInstance));
    }

    protected final JeSTResult<O> processResponse(
            final ReSTOperation operation,
            final Response response
    ) {
        try {
            final Class<O> responseClass = (Class<O>) findOperationMapping(clientInstance, operation, response).responseClass();
            return new JeSTResult<>(responseClass, response.readEntity(responseClass));
        } catch (NoMappingDefinedException e) {
            return null;
        }
    }

    void print(ReSTClient reSTClient) {
        System.err.println(stringify(reSTClient));
    }

    private String stringify(ReSTClient reSTClient) {
        return reSTClient.protocol().toString() +
                reSTClient.contextPath() + "\n" +
                stringify(reSTClient.defaultMappings());
    }

    private String stringify(ReSTOperationMapping[] reSTOperationMappings) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(reSTOperationMappings)
                .forEach(mapping -> builder.append(stringify(mapping)));
        return builder.toString();
    }

    private String stringify(ReSTOperationMapping mapping) {
        return mapping.statusCode() + mapping.responseClass().getSimpleName();
    }

    protected ReSTOperation getReSTOperation() {
        return findReSTOperation(clientInstance, methodName, request.getClass());
    }
}
