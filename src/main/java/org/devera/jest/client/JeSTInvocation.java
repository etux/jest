package org.devera.jest.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

public class JeSTInvocation<I, O> {

    private final Configuration configuration;
    private final Object clientInstance;
    private final String methodName;
    private final I request;
    private final Client jaxrsClient;

    public JeSTInvocation(
            final Configuration configuration,
            final Object clientInstance,
            final String methodName,
            final I request,
            final Client jaxrsClient
    ) {
        this.configuration = configuration;
        this.clientInstance = clientInstance;
        this.methodName = methodName;
        this.request = request;
        this.jaxrsClient = jaxrsClient;
    }


    public JeSTResult<O> invoke() {
        final ReSTOperation operation = findReSTOperation(methodName, request.getClass().getInterfaces()[0]);
        final Invocation invocation = getApplicationWebTarget()
                .request()
                .build(operation.method());
        return processResponse(operation, invocation.invoke());
    }

    private <R> ReSTOperation findReSTOperation(final String methodName, final Class<R> request) {
        return getClassWithAnnotationStream()
                .map(getMethodSafely(methodName, request))
                .map(method -> method.getAnnotation(ReSTOperation.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTOperation.class));
    }

    private Stream<Class<?>> getClassWithAnnotationStream() {
        return Arrays.stream(clientInstance.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class));
    }

    private <R> Function<Class<?>, Method> getMethodSafely(String methodName, Class<R> requestClass) {
        return clazz -> {
            try {
                return clazz.getMethod(methodName, requestClass);
            } catch (NoSuchMethodException e) {
                return null;
            }
        };
    }

    private Predicate<Class<?>> hasAnnotation(final Class annotation) {
        return clazz -> clazz.getAnnotation(annotation) != null;
    }

    private JeSTResult<O> processResponse(
            final ReSTOperation operation,
            final Response response
    ) {
        try {
            final Class<O> responseClass = (Class<O>) findOperationMapping(operation, response).responseClass();
            return new JeSTResult<>(responseClass, response.readEntity(responseClass));
        } catch (NoMappingDefinedException e) {
            return null;
        }
    }

    private ReSTOperationMapping findOperationMapping(ReSTOperation operation, Response response) {
        return getOperationMappingForResponse(
                getReSTOperationMappingsStream(operation.mappings()),
                response)
                .orElseGet(getReSTOperationMappingFromClientSupplier(operation, response));
    }

    private Supplier<ReSTOperationMapping> getReSTOperationMappingFromClientSupplier(ReSTOperation operation, Response response) {
        return () -> getReSTOperationMappingsStream(checkAndAssign(clientInstance).defaultMappings())
                .filter(getReSTOperationMappingPredicate(response))
                .findFirst()
                .orElseThrow(() -> new NoMappingDefinedException(clientInstance, operation, response.getStatus()));
    }

    private Predicate<ReSTOperationMapping> getReSTOperationMappingPredicate(final Response response) {
        return reSTOperationMapping -> reSTOperationMapping.statusCode() == response.getStatus();
    }

    private WebTarget getApplicationWebTarget() {
        return jaxrsClient.target(configuration.getApplicationUrl(clientInstance));
    }

    private Stream<ReSTOperationMapping> getReSTOperationMappingsStream(final ReSTOperationMapping[] mappings) {
        return Arrays.stream(mappings);
    }

    private Optional<ReSTOperationMapping> getOperationMappingForResponse(final Stream<ReSTOperationMapping> mappings,
                                                                          final Response response) {
        return mappings.filter(mapping -> mapping.statusCode() == response.getStatus()).findFirst();
    }

    private ReSTClient checkAndAssign(Object client) {
        Preconditions.checkArgument(getAnnotationFromClient(client) != null);
        return getAnnotationFromClient(client);
    }

    private ReSTClient getAnnotationFromClient(final Object client) {
        return Arrays.stream(client.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class))
                .findFirst()
                .map(clientInterface -> clientInterface.getAnnotation(ReSTClient.class))
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTClient.class));
    }


    void print(ReSTClient reSTClient) {
        System.err.println(stringify(reSTClient));
    }

    private String stringify(ReSTClient reSTClient) {
        return reSTClient.protocol().toString() +
                reSTClient.contextPath() + "\n" +
                stringify(reSTClient.defaultMappings());
    }

    private String stringify(ReSTOperationMapping[] reSTOperationMappings) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(reSTOperationMappings)
                .forEach(mapping -> builder.append(stringify(mapping)));
        return builder.toString();
    }

    private String stringify(ReSTOperationMapping mapping) {
        return mapping.statusCode() + mapping.responseClass().getSimpleName();
    }
}
