package org.devera.jest.processor;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.devera.jest.annotations.ReSTOperation;

class ReSTOperationAnnotatedMethod {

    private final ExecutableElement element;
    private final ReSTOperation annotation;

    ReSTOperationAnnotatedMethod(final ExecutableElement element) {
        this.element = element;
        this.annotation = element.getAnnotation(ReSTOperation.class);
    }

    String getOperationMethodName() {
        return this.element.getSimpleName().toString();
    }

    List<Parameter> getArgumentMapNameAndType() {
        return element.getParameters().stream()
            .map(
                typeParameter -> new Parameter(
                        typeParameter.getSimpleName().toString(),
                        typeParameter.asType())
            ).collect(Collectors.toList());
    }

    ReSTOperation getAnnotation() {
        return this.annotation;
    }

    TypeMirror getReturnType() {
        return element.getReturnType();
    }

    class Parameter {

        private final TypeMirror typeMirror;
        private final String name;

        Parameter(String name, TypeMirror typeMirror) {
            this.name = name;
            this.typeMirror = typeMirror;
        }

        TypeMirror getTypeMirror() {
            return typeMirror;
        }

        String getName() {
            return name;
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
