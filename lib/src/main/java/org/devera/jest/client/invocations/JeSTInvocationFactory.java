package org.devera.jest.client.invocations;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.client.Client;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.params.NamedParam;
import org.devera.jest.client.ReflectionUtils;

public final class JeSTInvocationFactory {

    private JeSTInvocationFactory() {}

    public static <I, O> JeSTInvocation<I, O> create(
        final Client jaxrsClient,
        final Configuration configuration,
        final Object clientInstance,
        final String methodName,
        final I request,
        final Class<O> responseClass,
        final NamedParam... generatedNamedParams)
    {
        final ReSTOperation operation = ReflectionUtils.findReSTOperation(clientInstance, methodName, getRequestClassOrNull(request));
        final Map<String, Object> pathGeneratedNamedParams = ReflectionUtils.getPathParams(generatedNamedParams);
        final Map<String, Object> headerGeneratedNamedParams = ReflectionUtils.getHeaderParams(generatedNamedParams);
        final Map<String, Object> queryGeneratedNamedParams = ReflectionUtils.getQueryParams(generatedNamedParams);

        switch(operation.method()) {
            case GET:
                return new JeSTGetInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        headerGeneratedNamedParams,
                        pathGeneratedNamedParams,
                        queryGeneratedNamedParams,
                        request,
                        responseClass
                );
            case POST:
                return new JeSTPostInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        headerGeneratedNamedParams,
                        pathGeneratedNamedParams,
                        queryGeneratedNamedParams,
                        request,
                        responseClass
                );
            case DELETE:
                return new JeSTDeleteInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        headerGeneratedNamedParams,
                        pathGeneratedNamedParams,
                        queryGeneratedNamedParams,
                        request,
                        responseClass
                );
            case PUT:
                return new JeSTPutInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        headerGeneratedNamedParams,
                        pathGeneratedNamedParams,
                        queryGeneratedNamedParams,
                        request,
                        responseClass
                );
            case OPTIONS:
                return new JeSTOptionsInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        headerGeneratedNamedParams,
                        pathGeneratedNamedParams,
                        queryGeneratedNamedParams,
                        request,
                        responseClass

                );
            default:
                throw new IllegalArgumentException("Method " + operation.method() + " not supported.");
        }
    }

    private static <I> Class<?> getRequestClassOrNull(I request) {
        return Optional.ofNullable(request)
                .map(Object::getClass)
                .orElse(null);
    }

}
