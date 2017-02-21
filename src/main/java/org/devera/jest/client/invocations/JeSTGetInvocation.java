package org.devera.jest.client.invocations;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.ReflectionUtils;

import java.util.Map;

public class JeSTGetInvocation<I,O> extends JeSTInvocation<I,O> {

    JeSTGetInvocation(
            final Client jaxrsClient,
            final Configuration configuration,
            final Object clientInstance,
            final ReSTOperation reSTOperation,
            final I request,
            final Class<O> responseClass
    )
    {
        super(jaxrsClient, configuration, clientInstance, reSTOperation, request, responseClass);
    }

    @Override
    protected final Invocation prepareInvocation() {
        return
            resolveWebTarget()
                .request()
                .buildGet();
    }

    private WebTarget resolveWebTarget() {
        JeSTTarget jeSTTarget = new JeSTTarget(getApplicationWebTarget());
        jeSTTarget.resolvePathParams()
        WebTarget processedTarget = getApplicationWebTarget();
        processedTarget = resolvePathParams(processedTarget);
        return resolveQueryParams(processedTarget);
    }

    private WebTarget resolveQueryParams(WebTarget processedTarget)
    {
        final Map<String, ?> queryParams = ReflectionUtils.getQueryParams(request);
        for (String queryParamName : queryParams.keySet()) {
            processedTarget = processedTarget.queryParam(queryParamName, queryParams.get(queryParamName));
        }
        return processedTarget;
    }

    private WebTarget resolvePathParams(final WebTarget webTarget)
    {
        WebTarget processedTarget = webTarget;
        final Map<String, ?> pathParams = ReflectionUtils.getPathParams(request);
        for (String pathParam : pathParams.keySet()) {
            processedTarget = processedTarget.resolveTemplate(pathParam, pathParams.get(pathParam));
        }
        return processedTarget;
    }
}
