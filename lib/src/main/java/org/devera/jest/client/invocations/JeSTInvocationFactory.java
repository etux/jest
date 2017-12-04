package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;

import org.devera.jest.client.Configuration;

public final class JeSTInvocationFactory {

    private JeSTInvocationFactory() {}

    public static <I, O> JeSTInvocation<I, O> create(
        final Client jaxrsClient,
        final Configuration configuration,
        final JeSTInvocationHelper invocationHelper)
    {
        switch(invocationHelper.getReSTOperation().method()) {
            case GET:
                return new JeSTGetInvocation<>(
                        jaxrsClient,
                        configuration,
                        invocationHelper
                );
            case POST:
                return new JeSTPostInvocation<>(
                        jaxrsClient,
                        configuration,
                        invocationHelper
                );
            case DELETE:
                return new JeSTDeleteInvocation<>(
                        jaxrsClient,
                        configuration,
                        invocationHelper
                );
            case PUT:
                return new JeSTPutInvocation<>(
                        jaxrsClient,
                        configuration,
                        invocationHelper
                );
            case OPTIONS:
                return new JeSTOptionsInvocation<>(
                        jaxrsClient,
                        configuration,
                        invocationHelper

                );
            default:
                throw new IllegalArgumentException("Method " + invocationHelper.getReSTOperation().method() + " not supported.");
        }
    }

}
