package com.acme.client;

public class ConflictResponse implements TestResponse{

    private String conflictMessage;

    public String getConflictMessage() {
        return conflictMessage;
    }

    public void setConflictMessage(String conflictMessage) {
        this.conflictMessage = conflictMessage;
    }
}
