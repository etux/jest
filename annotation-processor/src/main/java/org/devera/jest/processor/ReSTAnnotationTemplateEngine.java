package org.devera.jest.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;
import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;
import org.devera.jest.client.Configuration;
import org.devera.jest.client.JeSTClient;
import org.devera.jest.client.JeSTResult;

@Slf4j
class ReSTAnnotationTemplateEngine {

    private static final boolean OPERATION_ANNOTATIONS_DISABLED = true;

    private Set<ReSTClientAnnotatedClass> reSTClients;

    void process(final RoundEnvironment roundEnv) {
        reSTClients = roundEnv
                .getElementsAnnotatedWith(ReSTClient.class)
                .stream()
                .map(this::processReSTClientAnnotation)
                .collect(Collectors.toSet());
    }

    private ReSTClientAnnotatedClass processReSTClientAnnotation(final Element element) {
        log.info("Processing client annotation {}.", element);
        return new ReSTClientAnnotatedClass((TypeElement) element);
    }

    String render(ReSTAnnotationProcessor.ClassCreator creator) {
        final StringBuilder builder = new StringBuilder();
        reSTClients.forEach(restClient -> render(restClient, creator.createWriter(restClient)));
        return builder.toString();
    }

    private void render(final ReSTClientAnnotatedClass reSTClientAnnotatedClass, final Writer builder) {

        final String reSTClientImplClassName = reSTClientAnnotatedClass.getReSTClientImplClassName();
        log.info("Rendering {}.", reSTClientImplClassName);
        TypeSpec reSTClient = TypeSpec.classBuilder(reSTClientImplClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeName.get(reSTClientAnnotatedClass.getReSTClientInterfaceType()))
                .addField(Configuration.class, "configuration", Modifier.PRIVATE, Modifier.FINAL)
                .addField(JeSTClient.class, "jeSTClient", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(renderConstructor())
                .addMethods(renderOperations(reSTClientAnnotatedClass.getOperations()))
                .build();

        JavaFile javaFile = JavaFile
                .builder(reSTClientAnnotatedClass.getPackageName(), reSTClient)
                .addStaticImport(
                        ReSTOperation.Operations.class,
                        "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"
                )
                .build();

        try {
            javaFile.writeTo(builder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                builder.close();
            } catch (IOException e) {
                log.error("While trying to close the builder.", e);
            }
        }
    }

    private MethodSpec renderConstructor() {
        return MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Configuration.class, "configuration", Modifier.FINAL)
                .addStatement("this.$N = $N", "configuration", "configuration")
                .addStatement("this.$N = new JeSTClient($N, this)", "jeSTClient", "configuration")
                .build();
    }

    private Iterable<MethodSpec> renderOperations(Set<ReSTOperationAnnotatedMethod> operations) {
        log.info("Operations: {}.", operations);

        return
                operations.stream().map(this::renderOperation).collect(Collectors.toSet());
    }

    private MethodSpec renderOperation(ReSTOperationAnnotatedMethod operation) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder(operation.getOperationMethodName());

        log.info("Rendering operation: {}.", operation.getOperationMethodName());

        if (!OPERATION_ANNOTATIONS_DISABLED) {
            builder.addAnnotation(renderReSTOperationAnnotation(operation));
        }

        final String invocationParameters = getInvocationParameters(operation);

        log.info("Invocation Parameters: {}.", invocationParameters);
        return builder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.get(operation.getReturnType()))
                .addParameters(renderParameters(operation))
                .addStatement(
                        "final $T<$T> result = jeSTClient.invoke(\""+operation.getOperationMethodName()+ "\", " + getInvocationParameters(operation) + ")",
                        getStatementParameterTypes(operation)
                )
                .addStatement("return result.getPayload()")
                .build();
    }

    private Object[] getStatementParameterTypes(ReSTOperationAnnotatedMethod operation) {
        List parameterTypes = new ArrayList();
        parameterTypes.add(JeSTResult.class);
        parameterTypes.add(TypeName.get(operation.getReturnType()));
        final Class[] operationParameterTypes = getOperationParameterTypes(operation);
        if (operationParameterTypes != null) {
            parameterTypes.addAll(Arrays.asList(operationParameterTypes));
        }

        parameterTypes.stream().map(parameterType -> "Object" + parameterType.toString()).forEach(System.out::println);

        return parameterTypes.toArray();
    }

    private Class[] getOperationParameterTypes(ReSTOperationAnnotatedMethod operation) {

        return operation
                .getArgumentMapNameAndType(ReSTOperationAnnotatedMethod.isParameter())
                .stream()
                .map(ReSTOperationAnnotatedMethod.Parameter::getTypeClassFromParameter)
                .toArray(Class[]::new);
    }

    private String getInvocationParameters(final ReSTOperationAnnotatedMethod operation ) {
        final Stream.Builder<String> builder = Stream.builder();

        builder
            .add(getRequestInvocationParameter(operation))
            .add(getResponseInvocationParameter(operation));

        operation.getArgumentMapNameAndType(ReSTOperationAnnotatedMethod.isParameter())
                .stream()
                .map(parameter -> getArgumentAsString(parameter, operation))
                .forEach(builder::add);

        return builder
            .build()
            .collect(Collectors.joining(", "));
    }

    private String getRequestInvocationParameter(final ReSTOperationAnnotatedMethod operation) {
        return operation.getRequestArgument()
                .map(ReSTOperationAnnotatedMethod.Parameter::getName)
                .orElse(null);
    }

    private String getResponseInvocationParameter(final ReSTOperationAnnotatedMethod operation) {
        return CodeBlock.of("$T.class", TypeName.get(operation.getReturnType())).toString();
    }

    private String getArgumentAsString(ReSTOperationAnnotatedMethod.Parameter parameter, ReSTOperationAnnotatedMethod method) {
        if (ReSTOperationAnnotatedMethod.Parameter.Type.BODY == parameter.getType()) {
            parameter.getName();
        }
        return ("new $T(\"" + parameter.getName() +"\", "+ parameter.getName() +")");
    }

    private AnnotationSpec renderReSTOperationAnnotation(ReSTOperationAnnotatedMethod operation) {
        final AnnotationSpec.Builder builder = AnnotationSpec.builder(ReSTOperation.class);

        if (operation.getAnnotation().path().length() > 0) {
            builder.addMember("path", "\"" + operation.getAnnotation().path() + "\"");
        }

        builder.addMember("method", operation.getAnnotation().method().name());

        Arrays.stream(operation.getAnnotation().mappings())
                .forEach(
                        mapping ->
                                builder.addMember(
                                        "mappings",
                                        "$L",
                                        getReSTOperationMappingAnnotationSpec(mapping)
                                )
                );

        return builder
                .build();
    }

    private AnnotationSpec getReSTOperationMappingAnnotationSpec(ReSTOperationMapping mapping) {
        final AnnotationSpec.Builder builder = AnnotationSpec.builder(ReSTOperationMapping.class);
        builder.addMember("statusCode", "\"" + mapping.statusCode() + "\"");
        if (getOperationClass(mapping::responseClass).isPresent()) {
            builder.addMember("responseClass", this.getOperationClass(mapping::responseClass).get());
        }
        if (getOperationClass(mapping::exceptionClass).isPresent()) {
            builder.addMember("exceptionClass", getOperationClass(mapping::exceptionClass).get());
        }
        return builder
                .build();
    }

    private Optional<String> getOperationClass(Supplier<Class> mapping) {
        String exceptionClass;
        try {
            exceptionClass = mapping.get().getSimpleName();
        } catch (MirroredTypeException exception) {
            exceptionClass = exception.getTypeMirror().toString();
        }

        return getReSTOperationMappingDefinedOrEmptyClass(exceptionClass);
    }

    private Optional<String> getReSTOperationMappingDefinedOrEmptyClass(String clazz) {
        if (ReSTOperationMapping.Undefined.class.getCanonicalName().equals(clazz)) {
            return Optional.empty();
        }
        return Optional.of(clazz + ".class");
    }

    private List<ParameterSpec> renderParameters(final ReSTOperationAnnotatedMethod operation) {

        return operation.getArgumentMapNameAndType(ReSTOperationAnnotatedMethod.all()).stream()
                .map((entry) -> ParameterSpec.builder(TypeName.get(entry.getTypeMirror()), entry.getName()).build())
                .collect(Collectors.toList());
    }

}
