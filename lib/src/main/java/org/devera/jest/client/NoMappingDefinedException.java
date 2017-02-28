package org.devera.jest.client;

import org.devera.jest.annotations.ReSTOperation;

public class NoMappingDefinedException extends RuntimeException {

    NoMappingDefinedException(Object clientInstance, ReSTOperation operation, int status) {
        super(
                clientInstance.getClass().getSimpleName() +
                        " has no mapping defined for operation " +
                        operationToString(operation) +
                        " and status " + status
        );
    }

    private static String operationToString(ReSTOperation operation) {
        final String format = "operation[path=%s, method=%s]";
        return String.format(format, operation.path(), operation.method());
    }
}
