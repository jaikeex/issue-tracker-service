package com.jaikeex.issuetrackerservice.service;
import com.jaikeex.issuetrackerservice.Dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.Dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IssueService {

    public static final String ISSUES_BY_TYPE = "issuesByType";
    public static final String ISSUES_BY_SEVERITY = "issuesBySeverity";
    public static final String ISSUES_BY_STATUS = "issuesByStatus";
    public static final String ISSUES_BY_PROJECT = "issuesByProject";
    IssueRepository repository;

    @Autowired
    public IssueService(IssueRepository repository) {
        this.repository = repository;
    }

    public Issue saveIssueToDatabase(Issue issue) {
        if(canBeCreated(issue)) {
            convertNewLinesInDescriptionToHtml(issue);
            repository.save(issue);
        }
        return issue;
    }

    public void deleteIssueById(Integer id) {
        repository.deleteById(id);
    }

    public Issue findIssueById(int id) {
        return repository.findIssueById(id);
    }

    public Issue findIssueByTitle(String title) {
        return repository.findIssueByTitle(title);
    }

    public List<Issue> findAllIssues() {
        List<Issue> allIssues = repository.findAll();
        Collections.reverse(allIssues); // in order to display newest reports at the top
        return allIssues;
    }

    public List<Issue> findAllIssuesByType(IssueType type) {
        return repository.findAllIssuesByType(type);
    }

    public List<Issue> findAllIssuesBySeverity(Severity severity) {
        return repository.findAllIssuesBySeverity(severity);
    }

    public List<Issue> findAllIssuesByStatus(Status status) {
        return repository.findAllIssuesByStatus(status);
    }

    public List<Issue> findAllIssuesByProject(Project project) {
        return repository.findAllIssuesByProject(project);
    }

    public List<Issue> filterIssues(FilterDto filterDto) {
        List<Issue> listOfIssuesToFilter = findAllIssues();
        return getFilteredResults(filterDto, listOfIssuesToFilter);
    }

    public Issue updateIssueWithNewProperties(Issue updatedIssue) {
        int id = updatedIssue.getId();
        changePropertiesInDatabaseById(updatedIssue, id);
        return repository.findIssueById(id);
    }

    public Issue updateIssueWithNewDescription(DescriptionDto descriptionDto) {
        descriptionDto.setDescription(replaceNewLinesWithHtml(descriptionDto.getDescription()));
        repository.updateIssueWithNewDescription(
                descriptionDto.getTitle(), descriptionDto.getDescription());
        return repository.findIssueByTitle(descriptionDto.getTitle());
    }

    private boolean canBeCreated(Issue issue) {
        Issue dbResponse = repository.findIssueByTitle(issue.getTitle());
        if (dbResponse != null) {
            throw new TitleAlreadyExistsException();
        } else {
            return true;
        }
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
                initializeDefaultResults(listOfIssuesToFilter);
        return searchDatabaseForResultsByProperty(
                filterDto, defaultResults);
    }

    private Map<String, List<Issue>> searchDatabaseForResultsByProperty(
            FilterDto filterDto, Map<String,
            List<Issue>> issuesGroupedByProperty) {
        if (filterDto.getType() != null) {
            issuesGroupedByProperty.replace(ISSUES_BY_TYPE,
                    findAllIssuesByType(filterDto.getType()));
        }
        if (filterDto.getSeverity() != null) {
            issuesGroupedByProperty.replace(ISSUES_BY_SEVERITY,
                    findAllIssuesBySeverity(filterDto.getSeverity()));
        }
        if (filterDto.getStatus() != null) {
            issuesGroupedByProperty.replace(ISSUES_BY_STATUS,
                    findAllIssuesByStatus(filterDto.getStatus()));
        }
        if (filterDto.getProject() != null) {
            issuesGroupedByProperty.replace(ISSUES_BY_PROJECT,
                    findAllIssuesByProject(filterDto.getProject()));
        }
        return issuesGroupedByProperty;
    }

    private List<Issue> retainOnlyExactMatches(
            List<Issue> listOfIssuesToFilter,
            Map<String, List<Issue>> resultsFilteredByProperty) {

        listOfIssuesToFilter.retainAll(
                resultsFilteredByProperty.get(ISSUES_BY_TYPE));
        listOfIssuesToFilter.retainAll(
                resultsFilteredByProperty.get(ISSUES_BY_SEVERITY));
        listOfIssuesToFilter.retainAll(
                resultsFilteredByProperty.get(ISSUES_BY_STATUS));
        listOfIssuesToFilter.retainAll(
                resultsFilteredByProperty.get(ISSUES_BY_PROJECT));

        return listOfIssuesToFilter;
    }

    private Map<String, List<Issue>> initializeDefaultResults(
            List<Issue> listOfIssuesToFilter) {
        Map<String, List<Issue>> defaultFilterResults =
                new HashMap<>();

        defaultFilterResults.put(ISSUES_BY_TYPE, listOfIssuesToFilter);
        defaultFilterResults.put(ISSUES_BY_SEVERITY, listOfIssuesToFilter);
        defaultFilterResults.put(ISSUES_BY_STATUS, listOfIssuesToFilter);
        defaultFilterResults.put(ISSUES_BY_PROJECT, listOfIssuesToFilter);

        return defaultFilterResults;
    }

    private void changePropertiesInDatabaseById(Issue updatedIssue, int id) {
        repository.updateIssueWithNewType(id, updatedIssue.getType());
        repository.updateIssueWithNewSeverity(id, updatedIssue.getSeverity());
        repository.updateIssueWithNewStatus(id, updatedIssue.getStatus());
        repository.updateIssueWithNewProject(id, updatedIssue.getProject());
    }

    private void convertNewLinesInDescriptionToHtml(Issue issue) {
        issue.setDescription(replaceNewLinesWithHtml(issue.getDescription()));
    }

    private String replaceNewLinesWithHtml(String text) {
        return text.replaceAll("(\r\n|\n)", "<br />");
    }

}
