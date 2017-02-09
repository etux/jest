package org.devera.jest.annotations;

public enum Protocol {

    HTTP(80),
    HTTPS(443);

    private final int defaultPortNumber;

    Protocol(final int defaultPortNumber) {
        this.defaultPortNumber = defaultPortNumber;
    }

    public int getDefaultPortNumber() {
        return defaultPortNumber;
    }
}
