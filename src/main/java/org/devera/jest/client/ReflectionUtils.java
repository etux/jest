package org.devera.jest.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

class ReflectionUtils {

    private ReflectionUtils(){}

    static <R> ReSTOperation findReSTOperation(final Object clientInstance, final String methodName, final Class<R> request) {
        return getClassWithAnnotationStream(clientInstance)
                .map(getMethodSafely(methodName, request))
                .map(method -> method.getAnnotation(ReSTOperation.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTOperation.class));
    }

    static ReSTOperationMapping findOperationMapping(Object clientInstance, ReSTOperation operation, Response response) {
        return getOperationMappingForResponse(
                getReSTOperationMappingsStream(operation.mappings()),
                response)
                .orElseGet(getReSTOperationMappingFromClientSupplier(clientInstance, operation, response));
    }

    private static Stream<Class<?>> getClassWithAnnotationStream(final Object clientInstance) {
        return Arrays.stream(clientInstance.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class));
    }

    private static <R> Function<Class<?>, Method> getMethodSafely(String methodName, Class<R> requestClass) {
        return clazz -> {
            try {
                return clazz.getMethod(methodName, requestClass);
            } catch (NoSuchMethodException e) {
                return null;
            }
        };
    }

    private static Predicate<Class<?>> hasAnnotation(final Class annotation) {
        return clazz -> clazz.getAnnotation(annotation) != null;
    }

    private static Supplier<ReSTOperationMapping> getReSTOperationMappingFromClientSupplier(Object clientInstance, ReSTOperation operation, Response response) {
        return () -> getReSTOperationMappingsStream(checkAndAssign(clientInstance).defaultMappings())
                .filter(getReSTOperationMappingPredicate(response))
                .findFirst()
                .orElseThrow(() -> new NoMappingDefinedException(clientInstance, operation, response.getStatus()));
    }

    private static Predicate<ReSTOperationMapping> getReSTOperationMappingPredicate(final Response response) {
        return reSTOperationMapping -> reSTOperationMapping.statusCode() == response.getStatus();
    }

    private static Stream<ReSTOperationMapping> getReSTOperationMappingsStream(final ReSTOperationMapping[] mappings) {
        return Arrays.stream(mappings);
    }

    private static Optional<ReSTOperationMapping> getOperationMappingForResponse(final Stream<ReSTOperationMapping> mappings,
                                                                         final Response response) {
        return mappings.filter(mapping -> mapping.statusCode() == response.getStatus()).findFirst();
    }

    private static ReSTClient checkAndAssign(Object client) {
        Preconditions.checkArgument(getAnnotationFromClient(client) != null);
        return getAnnotationFromClient(client);
    }

    private static ReSTClient getAnnotationFromClient(final Object client) {
        return Arrays.stream(client.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class))
                .findFirst()
                .map(clientInterface -> clientInterface.getAnnotation(ReSTClient.class))
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTClient.class));
    }
}
