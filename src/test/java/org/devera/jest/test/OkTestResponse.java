package org.devera.jest.test;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OkTestResponse implements TestResponse {

    private String message;

    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
