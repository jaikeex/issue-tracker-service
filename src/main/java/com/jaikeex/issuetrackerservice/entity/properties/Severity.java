package com.jaikeex.issuetrackerservice.entity.properties;

public enum Severity {
    CRITICAL(0), HIGH(1), MEDIUM(2), LOW(3);

    private final int value;

    private Severity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
