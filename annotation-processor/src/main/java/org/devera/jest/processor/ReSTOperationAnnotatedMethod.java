package org.devera.jest.processor;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.devera.jest.annotations.ReSTHeaderParam;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTQueryParam;
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

    List<Parameter> getArgumentMapNameAndType() {
        return operationElement.getParameters().stream()
                .map(
                        typeParameter -> new Parameter(
                                typeParameter.getSimpleName().toString(),
                                typeParameter.asType(),
                                getType(typeParameter))
                ).collect(Collectors.toList());
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
        return Parameter.Type.PATH;
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
                    throw new IllegalStateException();
            }
        }

        static Class<? extends NamedParam> getTypeClassFromParameter(Parameter parameter) {
            return parameter.getTypeClassFromParameter();
        }

        public enum Type {
            QUERY,
            PATH,
            HEADER
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
