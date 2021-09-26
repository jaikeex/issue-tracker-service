package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.Dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.Dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.*;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IssueService {

    private static final String CACHE_NAME = "issue-cache-eh";

    IssueRepository repository;

    @Autowired
    public IssueService(IssueRepository repository) {
        this.repository = repository;
    }

    public Issue saveIssueToDatabase(Issue issue) {
        if(canBeCreated(issue)) {
            convertNewLinesInDescriptionToHtml(issue);
            repository.save(issue);
            log.info("Saved new issue report to the database [id={}] [title={}]", issue.getId(), issue.getTitle());
        }
        return issue;
    }

    public void deleteIssueById(Integer id) {
        repository.deleteById(id);
        log.info("Deleted issue from the database [id={}]", id);
    }

    @Cacheable(value = CACHE_NAME)
    public Issue findIssueById(int id) {
        return repository.findIssueById(id);
    }

    @Cacheable(value = CACHE_NAME)
    public Issue findIssueByTitle(String title) {
        return repository.findIssueByTitle(title);
    }

    @Cacheable(value = CACHE_NAME, key = "'findAllIssues'")
    public List<Issue> findAllIssues() {
        List<Issue> allIssues = repository.findAll();
        Collections.reverse(allIssues); // in order to display newest reports at the top
        return allIssues;
    }

    @Cacheable(value = CACHE_NAME)
    public List<Issue> findAllIssuesByType(IssueType type) {
        return repository.findAllIssuesByType(type);
    }

    @Cacheable(value = CACHE_NAME)
    public List<Issue> findAllIssuesBySeverity(Severity severity) {
        return repository.findAllIssuesBySeverity(severity);
    }

    @Cacheable(value = CACHE_NAME)
    public List<Issue> findAllIssuesByStatus(Status status) {
        return repository.findAllIssuesByStatus(status);
    }

    @Cacheable(value = CACHE_NAME)
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
        log.info("Updated the properties of an issue report in the database [id={}]", id);
        return repository.findIssueById(id);
    }

    public Issue updateIssueWithNewDescription(DescriptionDto descriptionDto) {
        descriptionDto.setDescription(replaceNewLinesWithHtml(descriptionDto.getDescription()));
        repository.updateIssueWithNewDescription(
                descriptionDto.getTitle(), descriptionDto.getDescription());
        log.info("Updated the description of an issue report in the database [title={}]", descriptionDto.getTitle());
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
                initializeDefaultFilterResults(listOfIssuesToFilter);
        return searchDatabaseForResultsByProperty(
                filterDto, defaultResults);
    }

    private Map<String, List<Issue>> searchDatabaseForResultsByProperty(
            FilterDto filterDto, Map<String,
            List<Issue>> issuesGroupedByProperty) {
        if (filterDto.getType() != null) {
            issuesGroupedByProperty.replace(Property.TYPE.getFilterMapKey(),
                    findAllIssuesByType(filterDto.getType()));
        }
        if (filterDto.getSeverity() != null) {
            issuesGroupedByProperty.replace(Property.SEVERITY.getFilterMapKey(),
                    findAllIssuesBySeverity(filterDto.getSeverity()));
        }
        if (filterDto.getStatus() != null) {
            issuesGroupedByProperty.replace(Property.STATUS.getFilterMapKey(),
                    findAllIssuesByStatus(filterDto.getStatus()));
        }
        if (filterDto.getProject() != null) {
            issuesGroupedByProperty.replace(Property.PROJECT.getFilterMapKey(),
                    findAllIssuesByProject(filterDto.getProject()));
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

    private void changePropertiesInDatabaseById(Issue updatedIssue, int id) {
        repository.updateIssueWithNewType(id, updatedIssue.getType());
        repository.updateIssueWithNewSeverity(id, updatedIssue.getSeverity());
        repository.updateIssueWithNewStatus(id, updatedIssue.getStatus());
        repository.updateIssueWithNewProject(id, updatedIssue.getProject());
    }

    private void convertNewLinesInDescriptionToHtml(Issue issue) {
        issue.setDescription(replaceNewLinesWithHtml(issue.getDescription()));
        log.debug("Converted new lines in issue description to <br> tags [id={}]", issue.getId());
    }

    private String replaceNewLinesWithHtml(String text) {
        return text.replaceAll("(\r\n|\n)", "<br />");
    }

}
