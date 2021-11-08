package com.jaikeex.issuetrackerservice;

import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.issue.IssueServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IssueRepositoryIntegrationTest {

    private static final String GENERAL_TEST_ISSUE_DESCRIPTION = "this is the general test ISSUE";
    private static final String UPDATE_TEST_ISSUE_DESCRIPTION = "this is the update test issue";
    private static final String FILTER_TEST_ISSUE_DESCRIPTION = "this is the filter test issue";
    private static final String GENERAL_TEST_AUTHOR = "general author";
    private static final String UPDATE_TEST_AUTHOR = "update author";
    private static final String FILTER_TEST_AUTHOR = "filter author";
    private static final String GENERAL_TEST_TITLE = "testTitle";
    private static final String UPDATE_TEST_TITLE = "update title";
    private static final String FILTER_TEST_TITLE = "filter title";
    private static final String CACHE_NAME = "issue-cache-eh";

    @Autowired
    CacheManager cacheManager;

    @Autowired
    IssueRepository repository;

    @Autowired
    IssueServiceImpl service;

    Issue testIssue;
    Issue updateTestIssue;
    Issue filterTestIssue;


    @BeforeEach
    public void beforeEach(){
        cacheManager.getCache(CACHE_NAME).clear();

        initTestIssue();
        initUpdateTestIssue();
        initFilterTestIssue();

        repository.save(testIssue);
        repository.save(updateTestIssue);
        repository.save(filterTestIssue);

    }

    private Issue getCachedBook() {
        return (Issue) cacheManager.getCache(CACHE_NAME).get(GENERAL_TEST_TITLE);
    }

    private void initTestIssue() {
        testIssue = new Issue();
        testIssue.setId(1);
        testIssue.setTitle(GENERAL_TEST_TITLE);
        testIssue.setDescription(GENERAL_TEST_ISSUE_DESCRIPTION);
        testIssue.setAuthor(GENERAL_TEST_AUTHOR);
        testIssue.setType(IssueType.BUG);
        testIssue.setSeverity(Severity.CRITICAL);
        testIssue.setStatus(Status.SUBMITTED);
        testIssue.setProject(Project.MWP);
    }

    private void initUpdateTestIssue() {
        updateTestIssue = new Issue();
        updateTestIssue.setId(1);
        updateTestIssue.setTitle(UPDATE_TEST_TITLE);
        updateTestIssue.setDescription(UPDATE_TEST_ISSUE_DESCRIPTION);
        updateTestIssue.setAuthor(UPDATE_TEST_AUTHOR);
        updateTestIssue.setType(IssueType.ENHANCEMENT);
        updateTestIssue.setSeverity(Severity.HIGH);
        updateTestIssue.setStatus(Status.SOLVED);
        updateTestIssue.setProject(Project.TRACKER);
    }

    private void initFilterTestIssue() {
        filterTestIssue = new Issue();
        filterTestIssue.setId(1);
        filterTestIssue.setTitle(FILTER_TEST_TITLE);
        filterTestIssue.setDescription(FILTER_TEST_ISSUE_DESCRIPTION);
        filterTestIssue.setAuthor(FILTER_TEST_AUTHOR);
        filterTestIssue.setType(IssueType.BUG);
        filterTestIssue.setSeverity(Severity.HIGH);
        filterTestIssue.setStatus(Status.SOLVED);
        filterTestIssue.setProject(Project.MWP);
    }

    @Test
    public void findIssueByTitle_givenCacheableIssue_shouldCacheTheResults() {
        Issue issue = service.findIssueByTitle(GENERAL_TEST_TITLE);
        assertEquals(issue, getCachedBook());
    }
}
