package com.jaikeex.issuetrackerservice.entity.properties;

/**
 * Solution Status of the issue. New reports are automatically assigned
 * a SUBMITTED status, which can be changed to OPEN or SOLVED.
 */
public enum Status {
    SUBMITTED(0), OPEN(1), SOLVED(2);

    private final int value;

    private Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
