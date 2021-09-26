package com.jaikeex.issuetrackerservice.entity.properties;

public enum Property {
    TYPE("issuesByType"),
    SEVERITY("issuesBySeverity"),
    STATUS("issuesByStatus"),
    PROJECT("issuesByProject");

    private final String filterMapKey;

    Property(String filterMapKey) {
        this.filterMapKey = filterMapKey;
    }

    public String getFilterMapKey() {
        return this.filterMapKey;
    }

}
