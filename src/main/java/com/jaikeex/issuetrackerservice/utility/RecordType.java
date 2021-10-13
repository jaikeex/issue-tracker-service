package com.jaikeex.issuetrackerservice.utility;

import com.jaikeex.issuetrackerservice.entity.Issue;

public enum RecordType {
    CREATE("CREATED with properties: "),
    UPDATE_PROPERTIES("UPDATED with properties: "),
    UPDATE_DESCRIPTION("CHANGED DESCRIPTION");

    private final String value;

    RecordType(String value) {
        this.value = value;
    }

    public String getValue(Issue issue) {
        switch (this) {
            case CREATE:
            case UPDATE_PROPERTIES:
                return value + issue.propertiesToString();
            case UPDATE_DESCRIPTION:
                return value;
        }
        return value;
    }
}
