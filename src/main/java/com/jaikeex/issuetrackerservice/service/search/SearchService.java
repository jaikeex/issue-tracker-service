package com.jaikeex.issuetrackerservice.service.search;

import com.jaikeex.issuetrackerservice.entity.Issue;

import java.util.List;

public interface SearchService {

    /**
     * Searches all database entries and returns the ones containing the
     * provided query string.
     * @param query String which the returned issues must contain.
     * @return List of issue objects that contain the query in either their
     *          title, author or description properties
     */
    List<Issue> searchIssues (String query);
}
