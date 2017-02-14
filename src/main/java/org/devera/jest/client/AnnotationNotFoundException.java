package org.devera.jest.client;

public class AnnotationNotFoundException extends RuntimeException {

    private final Class annotationClass;

    public AnnotationNotFoundException(final Class annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class getAnnotationClass() {
        return annotationClass;
    }
}
