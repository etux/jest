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
    private final Class<?> responseClass;
    private final List<Class<?>> argumentClasses;
    private final Map<Integer, ReSTOperationMapping> mappings;

    ReSTOperationAnnotatedMethod(final ExecutableElement element) {
        this.element = element;
        responseClass = getResponseClass(element);
        argumentClasses = getArgumentClasses(element);
        mappings = getMappings(element);
    }

    private Class<? extends TypeMirror> getResponseClass(ExecutableElement element) {
        return element.getReturnType().getClass();
    }

    private Map<Integer, ReSTOperationMapping> getMappings(final ExecutableElement element) {
        return Arrays.stream(element.getAnnotation(ReSTOperation.class).mappings()).collect(Collectors.toMap(
                ReSTOperationMapping::statusCode,
                annotation -> annotation
        ));
    }

    private List<Class<?>> getArgumentClasses(final ExecutableElement element) {
        return element.getTypeParameters().stream().map(o -> o.asType().getClass()).collect(Collectors.toList());
    }
}
