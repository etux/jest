package com.acme.client;

import org.devera.jest.client.Configuration;

public class TestClientConfiguration extends Configuration {

    public TestClientConfiguration(
            String host,
            String protocol,
            String portNumber,
            String contextPath
    )
    {
        super(host, protocol, portNumber, contextPath);
    }
}
