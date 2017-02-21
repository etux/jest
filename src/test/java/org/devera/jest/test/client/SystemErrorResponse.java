package org.devera.jest.test.client;

import org.devera.jest.annotations.Response;

public class SystemErrorResponse implements Response{

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
