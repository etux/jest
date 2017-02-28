package org.devera.jest.test.client;

public class SystemErrorResponse implements Response{

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
