package org.devera.jest.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import org.stringtemplate.v4.ST;
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

        ST stringTemplate = new ST(getClientTemplateString());
        processReSTClientAnnotation(stringTemplate, roundEnv);
        processReSTOperation(stringTemplate, roundEnv);
        System.out.println(stringTemplate.render());
        return true;

    }

    private void processReSTOperation(final ST stringTemplate,
                                      final RoundEnvironment roundEnv) {

        for (final Element element : roundEnv.getElementsAnnotatedWith(ReSTOperation.class)) {
            note(element.getSimpleName().toString());
            for (final ReSTOperationMapping reSTOperationMapping : element.getAnnotation(ReSTOperation.class).mappings()) {
                note(String.valueOf(reSTOperationMapping.statusCode()));
                try {
                    note(reSTOperationMapping.responseClass().getName());
                } catch (MirroredTypeException exception) {
                    note(exception.getTypeMirror().toString());
                }

            }
        }

    }

    private void processReSTClientAnnotation(final ST stringTemplate,
                                             final RoundEnvironment roundEnv) {

        for (final Element element : roundEnv.getElementsAnnotatedWith(ReSTClient.class)) {
            note("prototype_interface: " + element.getSimpleName().toString());
            stringTemplate.add("prototype_interface", element.getSimpleName());
            note("prototype_package: " + element.asType().getClass().getPackage());
            stringTemplate.add("prototype_package", element.asType().getClass().getPackage());
            note(element.getAnnotation(ReSTClient.class).protocol().toString());
            note(element.getAnnotation(ReSTClient.class).contextPath());
        }
    }

    private void note(final String message) {
        processingEnv
                .getMessager()
                .printMessage(Diagnostic.Kind.NOTE, message);
    }

    private String getClientTemplateString() {
        try {
            return new String(
                    Files.readAllBytes(
                            Paths.get(
                                    ReSTAnnotationProcessor.class.getClassLoader().getResource("templates/ClientImplementation").toURI())));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
