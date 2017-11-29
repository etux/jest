package org.devera.jest.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTHeaderParam;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.annotations.ReSTPathParam;
import org.devera.jest.annotations.ReSTQueryParam;
import org.devera.jest.client.params.HeaderParam;
import org.devera.jest.client.params.NamedParam;
import org.devera.jest.client.params.PathParam;
import org.devera.jest.client.params.QueryParam;

public final class ReflectionUtils {

    private ReflectionUtils(){}

    /**
     * Given a client instance, a method name, a class representing the request payload, it finds the corresponding
     * ReSTOperation that it is annotated with.
     *
     * @param clientInstance instance of which the method is found.
     * @param methodName name of the method
     * @param bodyArgClass class of the method parameter
     * @return ReSTOperation annotation of the corresponding method.
     * @throws AnnotationNotFoundException in case the annotation was not found.
     */
    public static ReSTOperation findReSTOperation(final Object clientInstance, final String methodName, final Class bodyArgClass) {
        return getClassWithReSTClientAnnotationStream(clientInstance)
                .map(getMethodSafely(methodName, bodyArgClass))
                .map(method -> method.getAnnotation(ReSTOperation.class))
                .findFirst()
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTOperation.class, clientInstance.getClass()));
    }

    public static ReSTOperationMapping findOperationMapping(Object clientInstance, ReSTOperation operation, Predicate<ReSTOperationMapping> operationMatcher) {
        return
                getOperationMappingForResponse(
                    getReSTOperationMappingsStream(operation.mappings()),
                    operationMatcher
                )
                .orElseGet(
                        getReSTOperationMappingFromClientSupplier(
                                clientInstance,
                                operation,
                                operationMatcher
                        )
                );
    }

    private static Stream<Class<?>> getClassWithReSTClientAnnotationStream(final Object clientInstance) {
        return getStream(clientInstance.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class));
    }

    private static Function<Class<?>, Method> getMethodSafely(final String methodName, final Class bodyArgClass) {
        return clazz -> {
            if (bodyArgClass == null) {
                return Arrays.stream(clazz.getMethods())
                        .filter(isMethod(methodName))
                        .findFirst()
                        .orElseThrow(() -> new MethodNotFoundException(clazz, methodName, bodyArgClass));
            } else {
                return Arrays.stream(clazz.getMethods())
                        .filter(isMethod(methodName))
                        .filter((isMethodWithRequest()))
                        .filter(isMethodWithRequestClass(bodyArgClass))
                        .findFirst()
                        .orElseThrow(() -> new MethodNotFoundException(clazz, methodName, bodyArgClass));
            }

        };
    }

    private static Predicate<Method> isMethod(String methodName) {
        return method -> method.getName().equals(methodName);
    }

    private static Predicate<Method> isMethodWithRequest() {
        return method -> Arrays.stream(method.getParameters())
                .anyMatch(ReflectionUtils.hasAnnotation(ReSTQueryParam.class, ReSTHeaderParam.class, ReSTPathParam.class).negate());
    }

    private static Predicate<Method> isMethodWithRequestClass(final Class requestClass) {
        return method ->
                Arrays.stream(method.getParameterTypes())
                .anyMatch(clazz -> clazz.equals(requestClass));
    }

    private static Predicate<AnnotatedElement> hasAnnotation(final Class<? extends Annotation>... annotations) {
        return annotatedElement -> Arrays.stream(annotations).anyMatch(annotatedElement::isAnnotationPresent);
    }

    private static Supplier<ReSTOperationMapping> getReSTOperationMappingFromClientSupplier(Object clientInstance, ReSTOperation operation, Predicate<ReSTOperationMapping> operationMatcher) {
        return () ->
                getOperationMappingForResponse(getReSTOperationMappingsStream(checkAndAssign(clientInstance).defaultMappings()), operationMatcher)
                    .orElseThrow(() -> new NoMappingDefinedException(clientInstance, operation, operationMatcher));
    }

    private static <R> Stream<R> getStream(final R[] array) {
        return Arrays.stream(array);
    }

    private static Stream<ReSTOperationMapping> getReSTOperationMappingsStream(final ReSTOperationMapping[] mappings) {
        return getStream(mappings);
    }

    private static Optional<ReSTOperationMapping> getOperationMappingForResponse(final Stream<ReSTOperationMapping> mappings, Predicate<ReSTOperationMapping> reSTOperationMappingPredicate) {
        return mappings.filter(reSTOperationMappingPredicate).findFirst();
    }

    private static ReSTClient checkAndAssign(Object client) {
        Preconditions.checkArgument(getAnnotationFromClient(client) != null);
        return getAnnotationFromClient(client);
    }

    private static ReSTClient getAnnotationFromClient(final Object client) {
        return getStream(client.getClass().getInterfaces())
                .filter(hasAnnotation(ReSTClient.class))
                .findFirst()
                .map(clientInterface -> clientInterface.getAnnotation(ReSTClient.class))
                .orElseThrow(() -> new AnnotationNotFoundException(ReSTClient.class, client.getClass()));
    }

    public static <O> Class<O> getResponseClass(final ReSTOperationMapping operationMapping)
    {
        if (ReSTOperationMapping.Undefined.class.equals(operationMapping.exceptionClass())) return (Class<O>) operationMapping.responseClass();
        return operationMapping.exceptionClass();
    }

    static Method findMethod(final Object clientInstance,
                             final String methodName,
                             final Class parameterClass)
    {
        return getClassWithReSTClientAnnotationStream(clientInstance)
                    .map(getMethodSafely(methodName, parameterClass))
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

    public static Map<String, Object> getPathParams(Object pathParamsSource) {
        if (pathParamsSource instanceof NamedParam[]) {
            return getPathParams((NamedParam[]) pathParamsSource);
        }
        return getPathParamsFromRequest(pathParamsSource);
    }

    private static Map<String, Object> getPathParamsFromRequest(Object request) {
        if (request == null) {
            return new HashMap<>();
        }
        return getStream(request.getClass().getDeclaredFields())
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

    public static Map<String, Object> getQueryParams(NamedParam[] params) {
        return getParamsForType(params, QueryParam.class);
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

    public static Map<String, Object> getQueryParamsFromRequest(Object request)
    {
        if (request == null) {
            return new HashMap<>();
        }
        //TODO implement
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
        return field -> (field.getAnnotation(ReSTQueryParam.class) != null) || !isPathParam().test(field);
    }

    private static Predicate<Field> isNotNull(Object request)
    {
        return v -> getValue(request, v) != null;
    }

    private static <T> T getValue(final Object object, final Field f) {
        return
            Optional
                    .ofNullable((T) invokeGetterMethod(object, f))
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
            return getGetterMethod(object, f).invoke(object);
        } catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getGetterMethod(Object object, Field f) throws NoSuchMethodException
    {
        return object.getClass().getMethod(getGetterMethodName(f));
    }

    private static String getGetterMethodName(Field f)
    {
        final String s = "get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
        return s;
    }
}
