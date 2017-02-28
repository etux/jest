package org.devera.jest.processor;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.devera.jest.annotations.ReSTClient;

class ReSTAnnotationTemplateEngine {

    private Set<ReSTClientAnnotatedClass> reSTClients;

    void process(final RoundEnvironment roundEnv) {
        reSTClients = roundEnv
                .getElementsAnnotatedWith(ReSTClient.class)
                .stream()
                .map(this::processReSTClientAnnotation)
                .collect(Collectors.toSet());
    }

    private ReSTClientAnnotatedClass processReSTClientAnnotation(final Element element) {
        return new ReSTClientAnnotatedClass((TypeElement) element);
    }

    public String render() {
        return null;
    }
}
