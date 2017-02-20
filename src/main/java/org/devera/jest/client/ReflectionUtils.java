package org.devera.jest.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

public final class ReflectionUtils {

    private ReflectionUtils(){}

    public static <R> ReSTOperation findReSTOperation(final Object clientInstance, final String methodName, final R request) {
        return getClassWithAnnotationStream(clientInstance)
                .map(getMethodSafely(methodName, getClassOrNull(request)))
                .map(method -> method.getAnnotation(ReSTOperation.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTOperation.class));
    }

    public static ReSTOperationMapping findOperationMapping(Object clientInstance, ReSTOperation operation, Response response) {
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
                if (requestClass == null) return clazz.getMethod(methodName);
                else return clazz.getMethod(methodName, requestClass);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
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

    public static <O> Class<O> getResponseClass(final ReSTOperationMapping operationMapping)
    {
        if (Void.class.equals(operationMapping.exceptionClass())) return (Class<O>) operationMapping.responseClass();
        return operationMapping.exceptionClass();
    }

    public static <I> Method findMethod(final Object clientInstance, final String methodName, final I request) {
        return getClassWithAnnotationStream(clientInstance)
                    .map(getMethodSafely(methodName, getClassOrNull(request)))
                    .findFirst()
                    .orElse(null);
    }

    static String stringify(ReSTClient reSTClient) {
        return reSTClient.protocol().toString() +
                reSTClient.contextPath() + "\n" +
                stringify(reSTClient.defaultMappings());
    }

    static String stringify(ReSTOperationMapping[] reSTOperationMappings) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(reSTOperationMappings)
                .forEach(mapping -> builder.append(stringify(mapping)));
        return builder.toString();
    }

    static String stringify(ReSTOperationMapping mapping) {
        return mapping.statusCode() + mapping.responseClass().getSimpleName();
    }

    static <I> Class<I> getClassOrNull(I request) {
        if (request == null) {
            return null;
        }
        return (Class<I>) request.getClass();
    }

    public static Map<String, ?> getQueryParams(Object request)
    {
        return
            Arrays.stream(request.getClass().getDeclaredFields())
                .filter(isNotNull(request))
                .collect(
                    Collectors.toMap(
                        Field::getName,
                        field -> getValue(request, field))
                );
    }

    private static Predicate<Field> isNotNull(Object request)
    {
        return v -> getValue(request, v) != null;
    }

    private static <T> T getValue(final Object object, final Field f) {
        return
            Optional.ofNullable((T) invokeGetterMethod(object, f))
                .map(v -> getCollectionOrSingle(v))
                .orElse(null);
    }

    private static <T> T getCollectionOrSingle(T v)
    {
        return
            v.getClass().isInstance(Collection.class) ?
                (T) Arrays.asList(Collection.class.cast(v).toArray()) :
                v;
    }

    private static Object invokeGetterMethod(Object object, Field f)
    {
        try {
            return getGetterMethodMethod(object, f).invoke(object);
        } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getGetterMethodMethod(Object object, Field f) throws NoSuchMethodException
    {
        return object.getClass().getMethod(getGetterMethodName(f));
    }

    private static String getGetterMethodName(Field f)
    {
        return "get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
    }
}
