package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.Property;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FilterService {

    IssueRepository repository;
    IssueService issueService;

    @Autowired
    public FilterService(IssueRepository repository, IssueService issueService) {
        this.repository = repository;
        this.issueService = issueService;
    }

    /** Returns those issue reports from database that match a given set of properties.
     * @param filterDto Dto with values of those properties that should be matched by the filter.
     * @return List of issues matching the filter.
     */
    @Transactional(readOnly = true)
    public List<Issue> filterIssues(FilterDto filterDto) {
        List<Issue> listOfIssuesToFilter = repository.findAllIssues();
        return getFilteredResults(filterDto, listOfIssuesToFilter);
    }

    private List<Issue> getFilteredResults(
            FilterDto filterDto, List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> resultsFilteredByProperty =
                getResultsFilteredByProperty(filterDto, listOfIssuesToFilter);
        return retainOnlyExactMatches(
                listOfIssuesToFilter, resultsFilteredByProperty);
    }

    private Map<String, List<Issue>> getResultsFilteredByProperty(
            FilterDto filterDto, List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> defaultResults =
                initializeDefaultFilterResults(listOfIssuesToFilter);
        return searchDatabaseForResultsByProperty(
                filterDto, defaultResults);
    }

    private Map<String, List<Issue>> searchDatabaseForResultsByProperty(
            FilterDto filterDto, Map<String,
            List<Issue>> issuesGroupedByProperty) {
        if (filterDto.getType() != null) {
            issuesGroupedByProperty.replace(Property.TYPE.getFilterMapKey(),
                    repository.findAllIssuesByType(filterDto.getType()));
        }
        if (filterDto.getSeverity() != null) {
            issuesGroupedByProperty.replace(Property.SEVERITY.getFilterMapKey(),
                    repository.findAllIssuesBySeverity(filterDto.getSeverity()));
        }
        if (filterDto.getStatus() != null) {
            issuesGroupedByProperty.replace(Property.STATUS.getFilterMapKey(),
                    repository.findAllIssuesByStatus(filterDto.getStatus()));
        }
        if (filterDto.getProject() != null) {
            issuesGroupedByProperty.replace(Property.PROJECT.getFilterMapKey(),
                    repository.findAllIssuesByProject(filterDto.getProject()));
        }
        return issuesGroupedByProperty;
    }

    private List<Issue> retainOnlyExactMatches(
            List<Issue> listOfIssuesToFilter,
            Map<String, List<Issue>> resultsFilteredByProperty) {

        for (Property property : Property.values()) {
            listOfIssuesToFilter.retainAll(
                    resultsFilteredByProperty.get(property.getFilterMapKey()));
        }
        return listOfIssuesToFilter;
    }

    private Map<String, List<Issue>> initializeDefaultFilterResults(
            List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> defaultFilterResults = new HashMap<>();
        for (Property property : Property.values()) {
            defaultFilterResults.put(property.getFilterMapKey(), listOfIssuesToFilter);
        }
        return defaultFilterResults;
    }

}
