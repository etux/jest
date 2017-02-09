package org.devera.jest.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

@SupportedAnnotationTypes({
        "org.devera.jest.annotations.ReSTClient",
        "org.devera.jest.annotations.ReSTOperation"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ReSTAnnotationProcessor extends AbstractProcessor {

    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        Class<ReSTClient> annotation = ReSTClient.class;

        processReSTClientAnnotation(roundEnv, annotation);


        Class<ReSTOperation> restOperationClass = ReSTOperation.class;
        try {
            processReSTOperation(roundEnv, restOperationClass);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return true;

    }

    private void processReSTOperation(final RoundEnvironment roundEnv,
                                      final Class<ReSTOperation> restOperationClass) throws ClassNotFoundException {

        for (final Element element : roundEnv.getElementsAnnotatedWith(restOperationClass)) {
            note(element.getSimpleName().toString());
            for (final ReSTOperationMapping reSTOperationMapping : element.getAnnotation(ReSTOperation.class).mappings()) {
                note(String.valueOf(reSTOperationMapping.statusCode()));
                try {
                    note(reSTOperationMapping.responseClass().getName());
                } catch (MirroredTypeException exception) {
                    exception.printStackTrace();
                }

            }
        }

    }

    private void processReSTClientAnnotation(RoundEnvironment roundEnv, Class<ReSTClient> annotation) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            note(element.getSimpleName().toString());
            note(element.getAnnotation(annotation).protocol().toString());
            note(element.getAnnotation(annotation).contextPath());
        }
    }

    private void note(final String message) {
        processingEnv
                .getMessager()
                .printMessage(Diagnostic.Kind.NOTE, message);
    }
}
