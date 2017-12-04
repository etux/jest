package org.devera.jest.client.invocations;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.client.ReflectionUtils;
import org.devera.jest.client.params.NamedParam;

public class JeSTInvocationHelper<I, O> {

    private final NamedParam[] namedParams;
    private final Class<O> responseClass;
    private final I request;
    private final String  methodName;
    private final Object clientInstance;

    public JeSTInvocationHelper(
            final Object clientInstance,
            final String methodName,
            final I request,
            final Class<O> responseClass,
            final NamedParam... namedParams
    )
    {
        this.namedParams = namedParams;
        this.responseClass = responseClass;
        this.request = request;
        this.methodName = methodName;
        this.clientInstance = clientInstance;
    }

    public NamedParam[] getNamedParams() {
        return namedParams;
    }

    public Class<O> getResponseClass() {
        return responseClass;
    }

    public I getRequest() {
        return request;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getClientInstance() {
        return clientInstance;
    }

    public ReSTOperation getReSTOperation() {
        return ReflectionUtils.findReSTOperation(clientInstance, methodName, getRequestClassOrNull(request));
    }

    private static <I> Class<?> getRequestClassOrNull(I request) {
        return Optional.ofNullable(request)
                .map(Object::getClass)
                .orElse(null);
    }

    Map<String, Object> getPathParams() {
        return
                Stream.of(
                    ReflectionUtils.getPathParams(request),
                    ReflectionUtils.getPathParams(namedParams)
                )
                .flatMap(
                        map -> map.entrySet().stream()
                )
                .collect(
                        Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (v1, v2) -> v1)
                );
    }

    public Map<String, Object> getHeaderParams() {
        return ReflectionUtils.getHeaderParams(namedParams);
    }

    public Map<String, Object> getQueryParams() {
        return
                Stream.of(
                        ReflectionUtils.getQueryParams(namedParams),
                        ReflectionUtils.getQueryParamsFromRequest(request))
                .flatMap(
                        map -> map.entrySet().stream()
                )
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (v1, v2) -> v1
                        )
                );
    }
}
