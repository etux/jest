package org.devera.jest.processor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.devera.jest.annotations.ReSTHeaderParam;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTPathParam;
import org.devera.jest.annotations.ReSTQueryParam;
import org.devera.jest.client.params.BodyParam;
import org.devera.jest.client.params.HeaderParam;
import org.devera.jest.client.params.NamedParam;
import org.devera.jest.client.params.PathParam;
import org.devera.jest.client.params.QueryParam;

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
                                typeParameter.getSimpleName().toString(),
                                typeParameter.asType(),
                                getType(typeParameter))
                ).collect(Collectors.toList());
    }

    public static Predicate<VariableElement> isParameter() {
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
            if (!hasAnyAnnotation(parameter, ReSTQueryParam.class, ReSTHeaderParam.class, ReSTQueryParam.class)) {
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
}
