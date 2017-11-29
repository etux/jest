package org.devera.jest.client;

import java.util.Optional;

class MethodNotFoundException extends RuntimeException {

    MethodNotFoundException(Class<?> clazz, String methodName, Class bodyArgClass) {
        super(
                String.format(
                        "Could not find method %s with request class %s in the given class %s",
                        Optional.ofNullable(methodName).orElse("no method"),
                        Optional.ofNullable(bodyArgClass).map(Class::getCanonicalName).orElse("no body arg class"),
                        Optional.ofNullable(clazz).map(Class::getCanonicalName).orElse("no class")
                )
        );
    }
}
