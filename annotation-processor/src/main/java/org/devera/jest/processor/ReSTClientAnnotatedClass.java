package org.devera.jest.processor;

import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.devera.jest.annotations.ReSTClient;

public class ReSTClientAnnotatedClass {

    private final TypeElement element;
    private final ReSTClient reSTClientAnnotation;
    private final Class<?> reSTClientInterface;

    ReSTClientAnnotatedClass(final TypeElement element) {
        this.element = element;
        this.reSTClientAnnotation = element.getAnnotation(ReSTClient.class);
        this.reSTClientInterface = reSTClientAnnotation.annotationType();
    }

    ReSTClient getReSTClientAnnotation() {
        return reSTClientAnnotation;
    }

    public Set<ReSTOperationAnnotatedMethod> getOperations() {
        return element.getEnclosedElements().stream()
                .map(ExecutableElement.class::cast)
                .map(ReSTOperationAnnotatedMethod::new)
                .collect(Collectors.toSet());
    }

    public TypeMirror getReSTClientInterfaceType() {
        return element.asType();
    }

    String getClassName() {
        return element.getSimpleName().toString();
    }

    String getQualifiedClassName() {
        return element.getQualifiedName().toString();
    }

    String getPackageName() {
        return getPackageFromQualifiedName();
    }

    private String getPackageFromQualifiedName() {
        return element.getQualifiedName()
                .toString()
                .substring(
                        0,
                        element.getQualifiedName().length() - (element.getSimpleName().length() + 1)
                );
    }

    String getReSTClientImplClassName() {
        return getClassName() + "Imp";
    }



    String getReSTClientImplQualifiedClassName() {
        return getQualifiedClassName() + "Imp";
    }
}
