package com.jaikeex.issuetrackerservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.issue.IssueServiceImpl;
import com.jaikeex.issuetrackerservice.service.search.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class SearchServiceTest {

    public static final String GENERAL_TEST_ISSUE_DESCRIPTION = "this is the general test ISSUE";
    public static final String UPDATE_TEST_ISSUE_DESCRIPTION = "this is the update test issue";
    public static final String FILTER_TEST_ISSUE_DESCRIPTION = "this is the filter test issue";
    public static final String GENERAL_TEST_AUTHOR = "general author";
    public static final String UPDATE_TEST_AUTHOR = "update author";
    public static final String FILTER_TEST_AUTHOR = "filter author";
    public static final String GENERAL_TEST_TITLE = "testTitle";
    public static final String UPDATE_TEST_TITLE = "update title";
    public static final String FILTER_TEST_TITLE = "filter title";
    public static final String NEW_DESCRIPTION = "new description";
    public static final String NEW_TITLE = "new title";

    @Mock
    IssueRepository repository;
    @Mock
    IssueServiceImpl issueService;

    @InjectMocks
    SearchServiceImpl service;

    Issue testIssue;
    Issue updateTestIssue;
    Issue filterTestIssue;

    List<Issue> findAllResults = new LinkedList<>();

    @BeforeEach
    public void beforeEach() throws JsonProcessingException {
        initTestIssue();
        initUpdateTestIssue();
        initFilterTestIssue();

        List<Issue> findAllResultsMock = getFindAllResults();
        when(repository.findAllIssues()).thenReturn(findAllResultsMock);

        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);

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
    public void searchIssues_shouldSearchDescriptionsForOccurrences() {
        assertEquals(
                Collections.singletonList(testIssue),
                service.searchIssues(GENERAL_TEST_ISSUE_DESCRIPTION)
        );
    }

    @Test
    public void searchIssues_shouldSearchAuthorsForOccurrences() {
        assertEquals(
                Collections.singletonList(filterTestIssue),
                service.searchIssues(FILTER_TEST_AUTHOR)
        );
    }

    @Test
    public void searchIssues_shouldSearchTitlesForOccurrences() {
        assertEquals(
                Collections.singletonList(updateTestIssue),
                service.searchIssues(UPDATE_TEST_TITLE)
        );
    }

    @Test
    public void searchIssues_shouldIgnoreCase() {
        assertEquals(
                Collections.singletonList(testIssue),
                service.searchIssues(GENERAL_TEST_ISSUE_DESCRIPTION
                        .toLowerCase())
        );
        assertEquals(
                Collections.singletonList(testIssue),
                service.searchIssues(GENERAL_TEST_ISSUE_DESCRIPTION
                        .toUpperCase())
        );
    }

    @Test
    public void searchIssues_givenNullQuery_shouldReturnAllIssues() {
        assertEquals(
                findAllResults,
                service.searchIssues(null)
        );
    }

    @Test
    public void searchIssues_givenEmptyQuery_shouldReturnAllIssues() {
        assertEquals(
                findAllResults,
                service.searchIssues("")
        );
    }

    private List<Issue> getFindAllResults() {
        List<Issue> findAllResults = new LinkedList<>();
        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);
        return findAllResults;
    }
}