package org.devera.jest.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

class ReSTOperationAnnotatedMethod {

    private final ExecutableElement element;
    private final ReSTOperation annotation;
    private final Map<Integer, ReSTOperationMapping> mappings;

    ReSTOperationAnnotatedMethod(final ExecutableElement element) {
        this.element = element;
        this.annotation = element.getAnnotation(ReSTOperation.class);
        mappings = getMappings(element);
    }

    private Map<Integer, ReSTOperationMapping> getMappings(final ExecutableElement element) {
        return Arrays.stream(element.getAnnotation(ReSTOperation.class).mappings()).collect(Collectors.toMap(
                ReSTOperationMapping::statusCode,
                annotation -> annotation
        ));
    }

    public String getOperationMethodName() {
        return this.element.getSimpleName().toString();
    }

    public List<Parameter> getArgumentMapNameAndType() {
        return element.getParameters().stream().map(
                typeParameter -> new Parameter(
                        typeParameter.getSimpleName().toString(),
                        typeParameter.asType())
                ).collect(Collectors.toList());
    }

    public ReSTOperation getAnnotation() {
        return this.annotation;
    }

    public TypeMirror getReturnType() {
        return element.getReturnType();
    }

    class Parameter {

        private final TypeMirror typeMirror;
        private final String name;

        Parameter(String name, TypeMirror typeMirror) {
            this.name = name;
            this.typeMirror = typeMirror;
        }

        public TypeMirror getTypeMirror() {
            return typeMirror;
        }

        public String getName() {
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
