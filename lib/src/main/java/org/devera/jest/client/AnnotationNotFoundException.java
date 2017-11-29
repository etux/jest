package org.devera.jest.client;

public class AnnotationNotFoundException extends RuntimeException {

    private final Class annotationClass;
    private final Class clientClass;

    AnnotationNotFoundException(final Class annotationClass, final Class clientClass) {
        super("Annotation class " +
                        annotationClass.getSimpleName() +
                        "not found on " +
                        clientClass.getSimpleName());
        this.annotationClass = annotationClass;
        this.clientClass = clientClass;
    }

    public Class getAnnotationClass() {
        return annotationClass;
    }

    public Class getClientClass() { return clientClass; }
}
