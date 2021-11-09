package com.jaikeex.issuetrackerservice.entity.issueProperties;

/**
 * The importance of the issue. Ranges from low to critical.
 */
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
