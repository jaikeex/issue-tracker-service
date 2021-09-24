package com.jaikeex.issuetrackerservice.entity.properties;

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
