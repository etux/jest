package org.devera.jest.processor;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.devera.jest.annotations.ReSTClient;

class ReSTAnnotationTemplateEngine {

    private Set<ReSTClientAnnotatedClass> reSTClients;

    void process(final RoundEnvironment roundEnv) {
        reSTClients = roundEnv
                .getElementsAnnotatedWith(ReSTClient.class)
                .stream()
                .filter(e -> e.getKind().isClass())
                .map(this::processReSTClientAnnotation)
                .collect(Collectors.toSet());
    }

    private ReSTClientAnnotatedClass processReSTClientAnnotation(final Element element) {
        return new ReSTClientAnnotatedClass((TypeElement) element);
    }

    String render() {
        final StringBuilder builder = new StringBuilder();
        reSTClients.forEach(restClient -> builder.append(render(restClient)));
        return builder.toString();
    }

    private String render(final ReSTClientAnnotatedClass reSTClientAnnotatedClass) {
        TypeSpec helloWorld = TypeSpec.classBuilder(reSTClientAnnotatedClass.getClassName())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .build();

        JavaFile javaFile = JavaFile.builder(reSTClientAnnotatedClass.getPackageName(), helloWorld)
            .build();

        StringBuilder builder = new StringBuilder();

        try {
            javaFile.writeTo(builder);
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
