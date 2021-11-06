package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.filter.FilterServiceImpl;
import com.jaikeex.issuetrackerservice.service.issue.IssueServiceImpl;
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
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FilterServiceImplTest {
    private static final String GENERAL_TEST_ISSUE_DESCRIPTION = "this is the general test ISSUE";
    private static final String UPDATE_TEST_ISSUE_DESCRIPTION = "this is the update test issue";
    private static final String FILTER_TEST_ISSUE_DESCRIPTION = "this is the filter test issue";
    private static final String GENERAL_TEST_AUTHOR = "general author";
    private static final String UPDATE_TEST_AUTHOR = "update author";
    private static final String FILTER_TEST_AUTHOR = "filter author";
    private static final String GENERAL_TEST_TITLE = "testTitle";
    private static final String UPDATE_TEST_TITLE = "update title";
    private static final String FILTER_TEST_TITLE = "filter title";
    private static final String NEW_DESCRIPTION = "new description";
    private static final String NEW_TITLE = "new title";
    @Mock
    IssueRepository repository;
    @Mock
    IssueServiceImpl issueService;

    @InjectMocks
    FilterServiceImpl service;

    Issue testIssue;
    Issue updateTestIssue;
    Issue filterTestIssue;
    IssueDto testFilterDto;

    List<Issue> findAllResults = new LinkedList<>();

    @BeforeEach
    public void beforeEach(){
        initTestIssue();
        initUpdateTestIssue();
        initFilterTestIssue();
        initFilterDto();

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

    private void initFilterDto() {
        testFilterDto = new IssueDto();
        testFilterDto.setType(IssueType.BUG);
        testFilterDto.setSeverity(null);
        testFilterDto.setStatus(Status.SUBMITTED);
        testFilterDto.setProject(Project.MWP);
    }

    @Test
    public void filterIssues_givenAllOk_shouldCallRepository() {
        when(issueService.findAllIssues()).thenReturn(findAllResults);
        service.filterIssues(testFilterDto);
        verify(repository, times(1)).findAllByType(IssueType.BUG);
        verify(repository, times(0)).findAllBySeverity(Severity.HIGH);
        verify(repository, times(1)).findAllByStatus(Status.SUBMITTED);
        verify(repository, times(1)).findAllByProject(Project.MWP);
    }

    @Test
    public void filterIssues_givenAllOk_shouldProcessResultsCorrectly() {
        List<Issue> findAllResults = getFindAllResults();

        List<Issue> findAllBugs = new LinkedList<>();
        findAllBugs.add(testIssue);
        findAllBugs.add(filterTestIssue);

        List<Issue> findAllMWP = new LinkedList<>();
        findAllMWP.add(testIssue);
        findAllMWP.add(filterTestIssue);

        List<Issue> findAllSubmitted = new LinkedList<>();
        findAllSubmitted.add(testIssue);

        when(repository.findAll()).thenReturn(findAllResults);
        when(repository.findAllByType(IssueType.BUG)).thenReturn(findAllBugs);
        when(repository.findAllByStatus(Status.SUBMITTED)).thenReturn(findAllSubmitted);
        when(repository.findAllByProject(Project.MWP)).thenReturn(findAllMWP);
        assertEquals(Collections.singletonList(testIssue), service.filterIssues(testFilterDto));
    }

    private List<Issue> getFindAllResults() {
        List<Issue> findAllResults = new LinkedList<>();
        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);
        return findAllResults;
    }

}