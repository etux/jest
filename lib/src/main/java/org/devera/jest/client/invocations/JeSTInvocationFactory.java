package org.devera.jest.client.invocations;

import java.lang.reflect.Method;
import java.util.Map;
import javax.ws.rs.client.Client;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.NamedParam;
import org.devera.jest.client.ReflectionUtils;

public final class JeSTInvocationFactory {

    private JeSTInvocationFactory() {}

    public static <I, O> JeSTInvocation<I, O> create(
        final Client jaxrsClient,
        final Configuration configuration,
        final Object clientInstance,
        final String methodName,
        final I request,
        final NamedParam... params)
    {

        final Method method = ReflectionUtils.findMethod(clientInstance, methodName);
        final ReSTOperation operation = ReflectionUtils.findReSTOperation(clientInstance, methodName, request);
        final Map<String, Object> pathParams = ReflectionUtils.getPathParams(params);

        switch(operation.method()) {
            case GET:
                return new JeSTGetInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        pathParams,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case POST:
                return new JeSTPostInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        pathParams,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case DELETE:
                return new JeSTDeleteInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        pathParams,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case PUT:
                return new JeSTPutInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        pathParams,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case OPTIONS:
                return new JeSTOptionsInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        pathParams,
                        request,
                        (Class<O>) method.getReturnType()

                );
            default:
                throw new IllegalArgumentException("Method " + operation.method() + " not supported.");
        }
    }

}
