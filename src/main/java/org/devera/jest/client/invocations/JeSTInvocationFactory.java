package org.devera.jest.client.invocations;

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

        ReSTOperation operation = ReflectionUtils.findReSTOperation(clientInstance, methodName, request);

        switch(operation.method()) {
            case GET:
                return new JeSTGetInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request
                );
            case POST:
                return new JeSTPostInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request
                );
            case DELETE:
                return new JeSTDeleteInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request
                );
            case PUT:
                return new JeSTPutInvocation<>(
                        jaxrsClient,
                        configuration,
                        clientInstance,
                        operation,
                        request
                );
            default:
                throw new IllegalArgumentException("Method " + operation.method() + " not supported.");
        }
    }

}
