package org.devera.jest.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@SupportedAnnotationTypes({
        "org.devera.jest.annotations.ReSTClient",
        "org.devera.jest.annotations.ReSTOperation",
        "org.devera.jest.annotations.ReSTOperationMapping"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ReSTAnnotationProcessor extends AbstractProcessor {

    private ReSTAnnotationTemplateEngine engine;
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        engine = new ReSTAnnotationTemplateEngine();
    }

    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        engine.process(roundEnv);
        System.out.println("Render: " + engine.render());
        return true;
    }
}
