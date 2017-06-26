package org.devera.jest.client;

import java.util.function.Predicate;

import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTOperationMapping;

class NoMappingDefinedException extends RuntimeException {

    NoMappingDefinedException(Object clientInstance, ReSTOperation operation, Predicate<ReSTOperationMapping> operationMatcher) {
        super(
                clientInstance.getClass().getSimpleName() +
                        " has no mapping defined for operation " +
                        operationToString(operation) +
                        " and matcher " + operationMatcher.getClass().getSimpleName()
        );
    }

    private static String operationToString(ReSTOperation operation) {
        final String format = "operation[path=%s, method=%s]";
        return String.format(format, operation.path(), operation.method());
    }
}
