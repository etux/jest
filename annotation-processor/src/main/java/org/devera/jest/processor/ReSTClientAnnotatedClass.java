package org.devera.jest.processor;

import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;

public class ReSTClientAnnotatedClass {

    private final TypeElement element;
    private final ReSTClient reSTClientAnnotation;
    private final Class<?> reSTClientInterface;
    private final Set<ReSTOperationAnnotatedMethod> operations;

    ReSTClientAnnotatedClass(final TypeElement element) {
        this.element = element;
        this.reSTClientAnnotation = element.getAnnotation(ReSTClient.class);
        this.reSTClientInterface = reSTClientAnnotation.annotationType();
        this.operations = getOperations(element);
    }

    ReSTClient getReSTClientAnnotation() {
        return reSTClientAnnotation;
    }

    Class<?> getReSTClientInterface() {
        return reSTClientInterface;
    }

    Set<ReSTOperationAnnotatedMethod> getOperations() {
        return operations;
    }

    private Set<ReSTOperationAnnotatedMethod> getOperations(final TypeElement element) {
        return element.getEnclosedElements()
                .stream()
                .filter(o -> o.getAnnotation(ReSTOperation.class) != null)
                .map(ExecutableElement.class::cast)
                .map(ReSTOperationAnnotatedMethod::new)
                .collect(Collectors.toSet());
    }

    String getClassName() {
        return reSTClientInterface.getSimpleName();
    }

    String getPackageName() {
        return reSTClientInterface.getPackage().getName();
    }
}
