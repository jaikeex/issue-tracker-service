package com.jaikeex.issuetrackerservice.entity.properties;

public enum Project {
    MWP(0), TRACKER(1), SUDOKU(2), DAP(3);

    private final int value;

    private Project(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
