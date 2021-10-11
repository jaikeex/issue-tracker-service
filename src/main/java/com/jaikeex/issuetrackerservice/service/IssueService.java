package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import com.jaikeex.issuetrackerservice.utility.htmlparser.HtmlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class IssueService {

    private static final String CACHE_NAME = "issue-cache-eh";

    private final HistoryService historyService;
    private final IssueRepository repository;
    private final CacheManager cacheManager;
    private final HtmlParser parser;

    @Autowired
    public IssueService(HistoryService historyService,
                        IssueRepository repository,
                        CacheManager cacheManager,
                        HtmlParser parser) {
        this.historyService = historyService;
        this.repository = repository;
        this.cacheManager = cacheManager;
        this.parser = parser;
    }

    public Issue saveIssueToDatabase(Issue issue) {
        if(canBeCreated(issue)) {
            parser.convertNewLinesInDescriptionToHtml(issue);
            repository.save(issue);
            log.info("Saved new issue report to the database [id={}] [title={}]", issue.getId(), issue.getTitle());
            historyService.record("created with the following properties: " + issue.propertiesToString(), issue);
        }
        clearAllCacheEntries();
        return issue;
    }

    public void deleteIssueById(Integer id) {
        repository.deleteById(id);
        log.info("Deleted issue from the database [id={}]", id);
        clearAllCacheEntries();
    }

    @Cacheable(value = CACHE_NAME, key = "'id' + #id")
    @Transactional(readOnly = true)
    public Issue findIssueById(int id) {
        return repository.findIssueById(id);
    }

    @Cacheable(value = CACHE_NAME, key = "'title' + #title")
    @Transactional(readOnly = true)
    public Issue findIssueByTitle(String title) {
        return repository.findIssueByTitle(title);
    }

    @Cacheable(value = CACHE_NAME, key = "'all'")
    @Transactional(readOnly = true)
    public List<Issue> findAllIssues() {
        return repository.findAllIssues();
    }

    @Cacheable(value = CACHE_NAME, key = "'type' + #type")
    @Transactional(readOnly = true)
    public List<Issue> findAllIssuesByType(IssueType type) {
        return repository.findAllIssuesByType(type);
    }

    @Cacheable(value = CACHE_NAME, key = "'severity' + #severity")
    @Transactional(readOnly = true)
    public List<Issue> findAllIssuesBySeverity(Severity severity) {
        return repository.findAllIssuesBySeverity(severity);
    }

    @Cacheable(value = CACHE_NAME, key = "'status' + #status")
    @Transactional(readOnly = true)
    public List<Issue> findAllIssuesByStatus(Status status) {
        return repository.findAllIssuesByStatus(status);
    }

    @Cacheable(value = CACHE_NAME, key = "'project' + #project")
    @Transactional(readOnly = true)
    public List<Issue> findAllIssuesByProject(Project project) {
        return repository.findAllIssuesByProject(project);
    }

    public Issue updateIssueWithNewProperties(Issue updatedIssue) {
        int id = updatedIssue.getId();
        changePropertiesInDatabaseById(updatedIssue, id);
        log.info("Updated the properties of an issue report in the database [id={}]", id);
        historyService.record("updated with the following properties: " + updatedIssue.propertiesToString(), updatedIssue);
        clearAllCacheEntries();
        return repository.findIssueById(id);
    }

    public Issue updateIssueWithNewDescription(DescriptionDto descriptionDto) {
        parser.convertNewLinesInDescriptionToHtml(descriptionDto);
        repository.updateIssueWithNewDescription(
                descriptionDto.getTitle(), descriptionDto.getDescription());
        log.info("Updated the description of an issue report in the database [title={}]",
                descriptionDto.getTitle());
        clearAllCacheEntries();
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

    private void changePropertiesInDatabaseById(Issue updatedIssue, int id) {
        repository.updateIssueWithNewType(id, updatedIssue.getType());
        repository.updateIssueWithNewSeverity(id, updatedIssue.getSeverity());
        repository.updateIssueWithNewStatus(id, updatedIssue.getStatus());
        repository.updateIssueWithNewProject(id, updatedIssue.getProject());
    }

    private void clearAllCacheEntries() {
        try {
            cacheManager.getCache(CACHE_NAME).clear();
        } catch (NullPointerException exception) {
            log.warn(exception.getMessage());
        }
    }
}
