package org.devera.jest.client;

import java.util.Optional;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTClient;

public class Configuration {

    private final String host;
    private final String protocol;
    private final String portNumber;
    private final String contextPath;

    public Configuration(
            final String host,
            final String protocol,
            final String portNumber,
            final String contextPath
    ) {
        Preconditions.checkNotNull(host);
        this.host = host;
        this.protocol = protocol;
        this.portNumber = portNumber;
        this.contextPath = contextPath;
    }

    final protected String getApplicationUrl(final Object client) {
        final ReSTClient reSTClient = client.getClass().getInterfaces()[0].getAnnotation(ReSTClient.class);
        return new StringBuilder()
                .append(Optional.ofNullable(protocol).orElse(reSTClient.protocol().name()))
                .append("://")
                .append(host)
                .append(":")
                .append(Optional.ofNullable(portNumber).orElse(String.valueOf(reSTClient.protocol().getDefaultPortNumber())))
                .append(Optional.ofNullable(contextPath).orElse(reSTClient.contextPath()))
                .toString();
    }
}
