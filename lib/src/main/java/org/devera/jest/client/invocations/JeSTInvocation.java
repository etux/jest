package org.devera.jest.client.invocations;

import java.util.Map;
import java.util.function.Predicate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.JeSTResult;
import org.devera.jest.client.ReflectionUtils;

import static org.devera.jest.client.ReflectionUtils.findOperationMapping;

public abstract class JeSTInvocation<I, O> {

    private final Client jaxrsClient;
    private final Configuration configuration;
    private final Object clientInstance;
    private final ReSTOperation reSTOperation;
    private final Map<String, Object> headerParams;
    private final Map<String, Object> pathParams;
    private final Class<O> responseClass;

    final I request;

    JeSTInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final Map<String, Object> headerParams,
            final Map<String, Object> pathParams,
            final I request,
            final Class<O> responseClass
    ) {
        this.configuration = configuration;
        this.clientInstance = clientInstance;
        this.reSTOperation = reSTOperation;
        this.headerParams = headerParams;
        this.pathParams = pathParams;
        this.request = request;
        this.jaxrsClient = jaxrsClient;
        this.responseClass = responseClass;
    }


    public final JeSTResult<O> invoke() {
        return processResponse(
                        prepareInvocation().invoke());
    }

    protected abstract Invocation prepareInvocation();

    final JeSTTarget resolveWebTarget() {
        return new JeSTTarget(getApplicationWebTarget())
            .resolveRequestPathParams(request, pathParams)
            .resolveRequestQueryParams(request)
            .resolveHeaderParams(headerParams);
    }

    final WebTarget getApplicationWebTarget() {
        return jaxrsClient.target(configuration.getApplicationUrl(clientInstance)).path(reSTOperation.path());
    }

    private JeSTResult<O> processResponse(final Response response)
    {
        final ReSTOperationMapping operationMapping = findOperationMapping(clientInstance, reSTOperation, responseMatcher(response));
        final Class<O> responseClass = getResponseClass(operationMapping);
        return new JeSTResult<>(responseClass, response.readEntity(responseClass));
    }

    private Class<O> getResponseClass(final ReSTOperationMapping operationMapping) {
        final Class<O> operationMappingResponseClass = ReflectionUtils.getResponseClass(operationMapping);
        if (ReSTOperationMapping.Undefined.class.equals(operationMappingResponseClass)) {
            return this.responseClass;
        }
        return operationMappingResponseClass;
    }

    private Predicate<ReSTOperationMapping> responseMatcher(Response response) {
        return mapping -> response.getStatus() == mapping.statusCode();
    }
}
