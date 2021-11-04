package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    IssueRepository repository;
    IssueService issueService;

    @Autowired
    public SearchService(IssueRepository repository, IssueService issueService) {
        this.repository = repository;
        this.issueService = issueService;
    }

    /**
     * Searches all database entries and returns the ones containing the
     * provided query string.
     * @param query String which the returned issues must contain.
     * @return List of issue objects that contain the query in either their
     *          title, author or description properties
     */
    @Transactional(readOnly = true)
    public List<Issue> searchIssues (String query) {
        List<Issue> listOfIssuesToSearch = repository.findAllIssues();
        log.debug("Initialized default search results list.");
        return handleEmptyQueryAndFetchResults(query, listOfIssuesToSearch);
    }

    private List<Issue> handleEmptyQueryAndFetchResults(
            String query, List<Issue> listOfIssuesToSearch) {
        if (query == null || query.equals("")) {
            log.debug("Search service called with empty query, returning default results");
            return listOfIssuesToSearch;
        } else {
            return getSearchResults(query, listOfIssuesToSearch);
        }
    }

    private List<Issue> getSearchResults(
            String query, List<Issue> listOfIssuesToSearch) {
        List<Issue> searchResults = listOfIssuesToSearch.stream()
                                    .filter(issue -> containsQuery(query, issue))
                                    .collect(Collectors.toList());
        log.debug("Searched database for all reports containing the query string [query={}]", query);
        return searchResults;
    }

    private boolean containsQuery(String query, Issue issue) {
        return checkForQueryOccurrence(query, issue.getDescription()) ||
                checkForQueryOccurrence(query, issue.getAuthor()) ||
                checkForQueryOccurrence(query, issue.getTitle());
    }

    private boolean checkForQueryOccurrence(String query, String textToSearch) {
        return StringUtils.containsIgnoreCase(textToSearch, query);
    }
}
