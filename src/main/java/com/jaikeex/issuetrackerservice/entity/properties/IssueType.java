package com.jaikeex.issuetrackerservice.entity.properties;

public enum IssueType {
    BUG(0), SUGGESTION(1), ENHANCEMENT(2);

    private final int value;

    private IssueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
