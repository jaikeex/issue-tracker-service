package com.jaikeex.issuetrackerservice.service.filter;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.Property;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Deprecated
public class FilterServiceImpl implements FilterService {

    private final IssueRepository repository;

    @Autowired
    public FilterServiceImpl(IssueRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Issue> filterIssues(IssueDto issueDto) {
        List<Issue> listOfIssuesToFilter = repository.findAll();
        return getFilteredResults(issueDto, listOfIssuesToFilter);
    }

    private List<Issue> getFilteredResults(
            IssueDto issueDto, List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> resultsFilteredByProperty =
                getResultsFilteredByProperty(issueDto, listOfIssuesToFilter);
        return retainOnlyExactMatches(
                listOfIssuesToFilter, resultsFilteredByProperty);
    }

    private Map<String, List<Issue>> getResultsFilteredByProperty(
            IssueDto issueDto, List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> defaultResults =
                initializeDefaultFilterResults(listOfIssuesToFilter);
        return searchDatabaseForResultsByProperty(
                issueDto, defaultResults);
    }

    private Map<String, List<Issue>> searchDatabaseForResultsByProperty(
            IssueDto issueDto, Map<String,
            List<Issue>> issuesGroupedByProperty) {
        if (issueDto.getType() != null) {
            issuesGroupedByProperty.replace(Property.TYPE.getFilterMapKey(),
                    repository.findAllByType(issueDto.getType()));
        }
        if (issueDto.getSeverity() != null) {
            issuesGroupedByProperty.replace(Property.SEVERITY.getFilterMapKey(),
                    repository.findAllBySeverity(issueDto.getSeverity()));
        }
        if (issueDto.getStatus() != null) {
            issuesGroupedByProperty.replace(Property.STATUS.getFilterMapKey(),
                    repository.findAllByStatus(issueDto.getStatus()));
        }
        if (issueDto.getProject() != null) {
            issuesGroupedByProperty.replace(Property.PROJECT.getFilterMapKey(),
                    repository.findAllByProject(issueDto.getProject()));
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
