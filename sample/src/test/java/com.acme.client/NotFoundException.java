package com.acme.client;

public class NotFoundException extends RuntimeException {

    private String exception;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
