package org.devera.jest.client.invocations;

import java.lang.reflect.Method;

import javax.ws.rs.client.Client;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.ReflectionUtils;

public final class JeSTInvocationFactory {

    private JeSTInvocationFactory() {}

    public static <I, O> JeSTInvocation<I, O> create(
            String methodName,
            I request,
            Configuration configuration,
            Object clientInstance,
            Client jaxrsClient) {

        final Method method = ReflectionUtils.findMethod(clientInstance, methodName, request);
        final ReSTOperation operation = ReflectionUtils.findReSTOperation(clientInstance, methodName, request);

        switch(operation.method()) {
            case GET:
                return new JeSTGetInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case POST:
                return new JeSTPostInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case DELETE:
                return new JeSTDeleteInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case PUT:
                return new JeSTPutInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request,
                        (Class<O>) method.getReturnType()
                );
            case OPTIONS:
                return new JeSTOptionsInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request,
                        (Class<O>) method.getReturnType()

                );
            default:
                throw new IllegalArgumentException("Method " + operation.method() + " not supported.");
        }
    }

}
