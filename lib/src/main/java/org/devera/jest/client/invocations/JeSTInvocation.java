package org.devera.jest.client.invocations;

import java.util.function.Predicate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.JeSTResult;
import org.devera.jest.client.ReflectionUtils;

import static org.devera.jest.client.ReflectionUtils.findOperationMapping;

public abstract class JeSTInvocation<I, O> {

    private final Client jaxrsClient;
    private final Configuration configuration;
    private final JeSTInvocationHelper<I, O> invocationHelper;

    JeSTInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final JeSTInvocationHelper<I,O> invocationHelper
    )
    {
        this.jaxrsClient = jaxrsClient;
        this.configuration = configuration;
        this.invocationHelper = invocationHelper;
    }


    public final JeSTResult<O> invoke() {
        return processResponse(
                        prepareInvocation().invoke());
    }

    protected abstract Invocation prepareInvocation();

    final JeSTTarget resolveWebTarget() {
        return new JeSTTarget(getApplicationWebTarget())
            .resolveRequestPathParams(invocationHelper)
            .resolveRequestQueryParams(invocationHelper)
            .resolveHeaderParams(invocationHelper);
    }

    final WebTarget getApplicationWebTarget() {
        return jaxrsClient
                .target(configuration.getApplicationUrl(invocationHelper.getClientInstance()))
                .path(invocationHelper.getReSTOperation().path());
    }

    private JeSTResult<O> processResponse(final Response response)
    {
        final Class<O> responseClass = getResponseClass(response);
        return new JeSTResult<>(responseClass, response.readEntity(responseClass));
    }

    private Class<O> getResponseClass(final Response response) {
        final ReSTOperationMapping operationMapping = findOperationMapping(invocationHelper.getClientInstance(), invocationHelper.getReSTOperation(), responseMatcher(response));
        final Class<O> operationMappingResponseClass = ReflectionUtils.getResponseClass(operationMapping);
        if (ReSTOperationMapping.Undefined.class.equals(operationMappingResponseClass)) {
            return invocationHelper.getResponseClass();
        }
        return operationMappingResponseClass;
    }

    private Predicate<ReSTOperationMapping> responseMatcher(Response response) {
        System.out.println("Finding matcher for response " + response.toString());
        return mapping -> response.getStatus() == mapping.statusCode();
    }

    protected I getRequest() {
        return invocationHelper.getRequest();
    }
}
