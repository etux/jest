package org.devera.jest.test;

public class OkTestResponse implements TestResponse {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
