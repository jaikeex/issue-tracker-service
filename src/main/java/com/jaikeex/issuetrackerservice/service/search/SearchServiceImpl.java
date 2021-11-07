package com.jaikeex.issuetrackerservice.service.search;

import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    IssueRepository issueRepository;
    IssueService issueService;

    @Autowired
    public SearchServiceImpl(IssueRepository issueRepository, IssueService issueService) {
        this.issueRepository = issueRepository;
        this.issueService = issueService;
    }

    @Override
    public List<Issue> searchIssues (String query) {
        List<Issue> listOfIssuesToSearch = issueRepository.findAllIssues();
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
        return listOfIssuesToSearch.stream()
                                    .filter(issue -> containsQuery(query, issue))
                                    .collect(Collectors.toList());
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
