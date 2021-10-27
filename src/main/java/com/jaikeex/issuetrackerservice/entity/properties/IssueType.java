package com.jaikeex.issuetrackerservice.entity.properties;

/**
 * Represents the purpose of the issue, whether it's a bug report,
 * or whether it suggests a new feature or an enhancement to an existing one.
 */
public enum IssueType {
    BUG(0), SUGGESTION(1), ENHANCEMENT(2);

    private final int value;

    IssueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
