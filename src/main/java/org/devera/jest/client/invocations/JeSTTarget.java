package org.devera.jest.client.invocations;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

public class JeSTTarget implements WebTarget
{
    private final WebTarget originalWebTarget;

    public JeSTTarget(final WebTarget originalWebTarget)
    {
        this.originalWebTarget = originalWebTarget;
    }



    @Override
    public URI getUri()
    {
        return originalWebTarget.getUri();
    }

    @Override
    public UriBuilder getUriBuilder()
    {
        return originalWebTarget.getUriBuilder();
    }

    @Override
    public WebTarget path(String path)
    {
        return originalWebTarget.path(path);
    }

    @Override
    public WebTarget resolveTemplate(String name, Object value)
    {
        return originalWebTarget.resolveTemplate(name, value);
    }

    @Override
    public WebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath)
    {
        return originalWebTarget.resolveTemplate(name, value, encodeSlashInPath);
    }

    @Override
    public WebTarget resolveTemplateFromEncoded(String name, Object value)
    {
        return originalWebTarget.resolveTemplate(name, value);
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues)
    {
        return originalWebTarget.resolveTemplates(templateValues);
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath)
    {
        return originalWebTarget.resolveTemplates(templateValues, encodeSlashInPath);
    }

    @Override
    public WebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues)
    {
        return originalWebTarget.resolveTemplatesFromEncoded(templateValues);
    }

    @Override
    public WebTarget matrixParam(String name, Object... values)
    {
        return originalWebTarget.matrixParam(name, values);
    }

    @Override
    public WebTarget queryParam(String name, Object... values)
    {
        return originalWebTarget.queryParam(name, values);
    }

    @Override
    public Invocation.Builder request()
    {
        return originalWebTarget.request();
    }

    @Override
    public Invocation.Builder request(String... acceptedResponseTypes)
    {
        return originalWebTarget.request(acceptedResponseTypes);
    }

    @Override
    public Invocation.Builder request(MediaType... acceptedResponseTypes)
    {
        return originalWebTarget.request(acceptedResponseTypes);
    }

    @Override
    public Configuration getConfiguration()
    {
        return originalWebTarget.getConfiguration();
    }

    @Override
    public WebTarget property(String name, Object value)
    {
        return originalWebTarget.property(name, value);
    }

    @Override
    public WebTarget register(Class<?> componentClass)
    {
        return originalWebTarget.register(componentClass);
    }

    @Override
    public WebTarget register(Class<?> componentClass, int priority)
    {
        return originalWebTarget.register(componentClass, priority);
    }

    @Override
    public WebTarget register(Class<?> componentClass, Class<?>[] contracts)
    {
        return originalWebTarget.register(componentClass, contracts);
    }

    @Override
    public WebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
    {
        return originalWebTarget.register(componentClass, contracts);
    }

    @Override
    public WebTarget register(Object component)
    {
        return originalWebTarget.register(component);
    }

    @Override
    public WebTarget register(Object component, int priority)
    {
        return originalWebTarget.register(component, priority);
    }

    @Override
    public WebTarget register(Object component, Class<?>[] contracts)
    {
        return originalWebTarget.register(component, contracts);
    }

    @Override
    public WebTarget register(Object component, Map<Class<?>, Integer> contracts)
    {
        return originalWebTarget.register(component, contracts);
    }
}
