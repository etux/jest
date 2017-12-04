package org.devera.jest.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import lombok.extern.slf4j.Slf4j;
import org.devera.jest.annotations.ReSTHeaderParam;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTPathParam;
import org.devera.jest.annotations.ReSTQueryParam;
import org.devera.jest.client.params.BodyParam;
import org.devera.jest.client.params.HeaderParam;
import org.devera.jest.client.params.NamedParam;
import org.devera.jest.client.params.PathParam;
import org.devera.jest.client.params.QueryParam;

@Slf4j
class ReSTOperationAnnotatedMethod {

    private final ExecutableElement operationElement;
    private final ReSTOperation annotation;

    ReSTOperationAnnotatedMethod(final ExecutableElement operationElement) {
        this.operationElement = operationElement;
        this.annotation = operationElement.getAnnotation(ReSTOperation.class);
    }

    String getOperationMethodName() {
        return this.operationElement.getSimpleName().toString();
    }

    List<Parameter> getArgumentMapNameAndType(Predicate<VariableElement> filter) {
        return operationElement.getParameters()
                .stream()
                .filter(filter)
                .map(
                        typeParameter -> new Parameter(
                                getName(typeParameter),
                                typeParameter.asType(),
                                getType(typeParameter))
                ).collect(Collectors.toList());
    }

    private static String getName(VariableElement typeParameter) {
        return NameSolverFactory.getSolver(typeParameter).getName();
    }

    private static boolean hasReSTPathParamValue(VariableElement typeParameter) {
        return typeParameter.getAnnotation(ReSTPathParam.class) != null &&
        !ReSTPathParam.EMPTY_VALUE.equals(typeParameter.getAnnotation(ReSTPathParam.class).value());
    }

    private static boolean hasReSTQueryParamValue(VariableElement typeParameter) {
        return typeParameter.getAnnotation(ReSTQueryParam.class) != null &&
        !ReSTQueryParam.EMPTY_VALUE.equals(typeParameter.getAnnotation(ReSTQueryParam.class).value());
    }

    static Predicate<VariableElement> isParameter() {
        return param -> hasAnyAnnotation(param, ReSTQueryParam.class, ReSTPathParam.class, ReSTHeaderParam.class);
    }

    private static boolean hasAnyAnnotation(VariableElement param, Class... annotations) {
        for (Class annotation : annotations) {
            if (param.getAnnotation(annotation) != null) return true;
        }
        return false;
    }

    ReSTOperation getAnnotation() {
        return this.annotation;
    }

    TypeMirror getReturnType() {
        return operationElement.getReturnType();
    }

    private static Parameter.Type getType(final VariableElement typeParameter) {
        if (typeParameter.getAnnotation(ReSTQueryParam.class) != null) return Parameter.Type.QUERY;
        if (typeParameter.getAnnotation(ReSTHeaderParam.class) != null) return Parameter.Type.HEADER;
        if (typeParameter.getAnnotation(ReSTPathParam.class) != null) return Parameter.Type.PATH;
        return Parameter.Type.BODY;
    }

    Optional<Parameter> getRequestArgument() {
        for (VariableElement parameter : this.operationElement.getParameters()) {
            if (!hasAnyAnnotation(parameter, ReSTPathParam.class, ReSTHeaderParam.class, ReSTQueryParam.class)) {
                return Optional.of(
                        new Parameter(
                                parameter.getSimpleName().toString(),
                                parameter.asType(),
                                getType(parameter)
                        )
                );
            }
        }
        return Optional.empty();
    }

    public static Predicate<VariableElement> all() {
        return param -> true;
    }

    static class Parameter {

        private Class<? extends NamedParam> getTypeClassFromParameter() {
            switch (getType()) {
                case HEADER:
                    return HeaderParam.class;
                case PATH:
                    return PathParam.class;
                case QUERY:
                    return QueryParam.class;
                default:
                    return BodyParam.class;
            }
        }

        static Class<? extends NamedParam> getTypeClassFromParameter(Parameter parameter) {
            return parameter.getTypeClassFromParameter();
        }

        public enum Type {
            QUERY,
            PATH,
            HEADER,
            BODY
        }

        private final TypeMirror typeMirror;
        private final String name;
        private final Type type;


        Parameter(String name, TypeMirror typeMirror, Type type) {
            this.name = name;
            this.typeMirror = typeMirror;
            this.type = type;
        }

        TypeMirror getTypeMirror() {
            return typeMirror;
        }

        String getName() {
            return name;
        }

        Type getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Parameter)) return false;

            Parameter parameter = (Parameter) o;

            if (!typeMirror.equals(parameter.typeMirror)) return false;
            return name.equals(parameter.name);
        }

        @Override
        public int hashCode() {
            int result = typeMirror.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private static class NameSolverFactory {
        static NameSolver getSolver(VariableElement typeParameter) {
            checkNoMoreThanOneAnnotation(typeParameter);

            if (hasReSTPathParamValue(typeParameter)) {
                return new AnnotationNameSolver<>(ReSTPathParam.class, typeParameter);
            }
            if (hasReSTQueryParamValue(typeParameter)) {
                return new AnnotationNameSolver<>(ReSTQueryParam.class, typeParameter);
            }
            return new ReflectionNameSolver(typeParameter);
        }

        private static void checkNoMoreThanOneAnnotation(VariableElement typeParameter) {
            if (
                    Arrays.asList(
                        ReSTQueryParam.class,
                        ReSTPathParam.class,
                        ReSTHeaderParam.class)
                        .stream()
                        .filter(annotation -> !Objects.isNull(typeParameter.getAnnotation(annotation)))
                        .count() > 1)
            {
                throw new IllegalArgumentException("Type parameter " + typeParameter + " has more than one ReST annotations");
            }
        }
    }

    private interface NameSolver {
        String getName();
    }

    private static class ReflectionNameSolver implements NameSolver {

        private final VariableElement typeParameter;

        ReflectionNameSolver(VariableElement typeParameter) {
            this.typeParameter = typeParameter;
        }

        @Override
        public String getName() {

            log.info(
                    "Getting name {} for parameter {} from reflection.",
                    typeParameter.getSimpleName().toString(),
                    typeParameter.getSimpleName().toString()
            );

            return typeParameter.getSimpleName().toString();
        }
    }

    private static class AnnotationNameSolver<T extends Annotation> implements NameSolver {

        private final Class<T> annotation;
        private final VariableElement typeParameter;

        AnnotationNameSolver(Class<T> annotation, VariableElement typeParameter) {
            this.annotation = annotation;
            this.typeParameter = typeParameter;
        }

        @Override
        public String getName() {

            log.info(
                    "Getting name {} for parameter from annotation {}.",
                    typeParameter.getSimpleName(),
                    typeParameter.getAnnotation(annotation)
            );

            try {
                final Method method = typeParameter.getAnnotation(annotation).annotationType().getMethod("value");
                log.info("Field {}.", method);

                final String o = String.class.cast(method.invoke(typeParameter.getAnnotation(annotation)));
                log.info("Field value {}.", o);

                return o;

            } catch (final NoSuchMethodException e) {
                throw new RuntimeException("Field is null, this is an error in the implementation of the annotation itself. All @ReST annotations must implement the value field.", e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Field value is null, this an error in the implementation of the annotation itself. All @ReST annotations must provide a default value for the value field.", e);
            }
        }
    }
}
