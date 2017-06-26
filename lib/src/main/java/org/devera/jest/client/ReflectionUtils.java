package org.devera.jest.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTPathParam;
import org.devera.jest.annotations.ReSTQueryParam;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.client.params.HeaderParam;
import org.devera.jest.client.params.NamedParam;
import org.devera.jest.client.params.PathParam;

public final class ReflectionUtils {

    private ReflectionUtils(){}

    public static <R> ReSTOperation findReSTOperation(final Object clientInstance, final String methodName, final R request) {
        return getClassWithReSTClientAnnotationStream(clientInstance)
                .map(getMethodSafely(methodName))
                .map(method -> method.getAnnotation(ReSTOperation.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTOperation.class));
    }

    public static ReSTOperationMapping findOperationMapping(Object clientInstance, ReSTOperation operation, Predicate<ReSTOperationMapping> operationMatcher) {
        return getOperationMappingForResponse(
                getReSTOperationMappingsStream(operation.mappings()),
                operationMatcher)
                .orElseGet(getReSTOperationMappingFromClientSupplier(clientInstance, operation, operationMatcher));
    }

    private static Stream<Class<?>> getClassWithReSTClientAnnotationStream(final Object clientInstance) {
        return Arrays.stream(clientInstance.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class));
    }

    private static <R> Function<Class<?>, Method> getMethodSafely(final String methodName) {
        return clazz ->
                Arrays.stream(clazz.getMethods())
                    .filter(method -> method.getName().equals(methodName))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
    }

    private static Predicate<Class<?>> hasAnnotation(final Class annotation) {
        return clazz -> clazz.getAnnotation(annotation) != null;
    }

    private static Supplier<ReSTOperationMapping> getReSTOperationMappingFromClientSupplier(Object clientInstance, ReSTOperation operation, Predicate<ReSTOperationMapping> operationMatcher) {
        return () -> getOperationMappingForResponse(getReSTOperationMappingsStream(checkAndAssign(clientInstance).defaultMappings()), operationMatcher)
                .orElseThrow(() -> new NoMappingDefinedException(clientInstance, operation, operationMatcher));
    }

    private static Predicate<ReSTOperationMapping> getReSTOperationMappingPredicate(final int status) {
        return reSTOperationMapping -> reSTOperationMapping.statusCode() == status;
    }

    private static Stream<ReSTOperationMapping> getReSTOperationMappingsStream(final ReSTOperationMapping[] mappings) {
        return Arrays.stream(mappings);
    }

    private static Optional<ReSTOperationMapping> getOperationMappingForResponse(final Stream<ReSTOperationMapping> mappings, Predicate<ReSTOperationMapping> reSTOperationMappingPredicate) {
        return mappings.filter(reSTOperationMappingPredicate).findFirst();
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
        if (ReSTOperationMapping.Undefined.class.equals(operationMapping.exceptionClass())) return (Class<O>) operationMapping.responseClass();
        return operationMapping.exceptionClass();
    }

    public static Method findMethod(final Object clientInstance,
                                        final String methodName)
    {
        return getClassWithReSTClientAnnotationStream(clientInstance)
                    .map(getMethodSafely(methodName))
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

    public static Map<String, Object> getPathParams(Object pathParamsSource) {
        if (pathParamsSource instanceof NamedParam[]) {
            return getPathParams((NamedParam[]) pathParamsSource);
        }
        return getPathParamsFromRequest(pathParamsSource);
    }

    private static Map<String, Object> getPathParamsFromRequest(Object request) {
        return Arrays.stream(request.getClass().getDeclaredFields())
            .filter(isNotNull(request))
            .filter(isPathParam())
            .collect(
                Collectors.toMap(
                    Field::getName,
                    field -> getValue(request, field)
                )
            );
    }

    public static Map<String, Object> getHeaderParams(NamedParam[] params) {
        return getParamsForType(params, HeaderParam.class);
    }

    private static Map<String, Object> getPathParams(NamedParam[] params) {
        return getParamsForType(params, PathParam.class);
    }

    private static Map<String, Object> getParamsForType(NamedParam[] request, Class<? extends NamedParam> namedParamType) {
        return Arrays
            .stream(request)
            .filter(namedParam -> namedParamType.isAssignableFrom(namedParam.getClass()))
            .collect(Collectors.toMap(
                NamedParam::getName,
                NamedParam::getValue
            ));
    }

    private static Predicate<? super Field> isPathParam()
    {
        return field -> field.getAnnotation(ReSTPathParam.class) != null;
    }

    public static Map<String, ?> getQueryParams(Object request)
    {
        return
            Arrays.stream(request.getClass().getDeclaredFields())
                .filter(isNotNull(request))
                .filter(isQueryParam())
                .collect(
                    Collectors.toMap(
                        Field::getName,
                        field -> getValue(request, field))
                );
    }

    private static Predicate<? super Field> isQueryParam()
    {
        return field -> field.getAnnotation(ReSTQueryParam.class) != null || !isPathParam().test(field);
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
