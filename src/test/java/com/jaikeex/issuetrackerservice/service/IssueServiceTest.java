package com.jaikeex.issuetrackerservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jaikeex.issuetrackerservice.Dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.Dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class IssueServiceTest {

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

    @InjectMocks
    IssueService service;

    Issue testIssue;
    Issue updateTestIssue;
    Issue filterTestIssue;
    FilterDto testFilterDto;
    DescriptionDto descriptionDto;

    List<Issue> findAllResults = new LinkedList<>();

    @BeforeEach
    public void beforeEach() throws JsonProcessingException {
        initTestIssue();
        initUpdateTestIssue();
        initFilterTestIssue();
        initFilterDto();
        initDescriptionDto();

        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);

    }

    private void initDescriptionDto() {
        descriptionDto = new DescriptionDto();
        descriptionDto.setDescription(NEW_DESCRIPTION);
        descriptionDto.setTitle(NEW_TITLE);
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
        testFilterDto = new FilterDto();
        testFilterDto.setType(IssueType.BUG);
        testFilterDto.setSeverity(null);
        testFilterDto.setStatus(Status.SUBMITTED);
        testFilterDto.setProject(Project.MWP);
    }


    @Test
    public void saveIssueToDatabase_givenAllOk_shouldCallRepository() {
        when(repository.findIssueByTitle(testIssue.getTitle())).thenReturn(null);
        service.saveIssueToDatabase(testIssue);
        verify(repository, times(1)).save(testIssue);
    }

    @Test
    public void saveIssueToDatabase_givenTitleAlreadyExists_shouldThrowException() {
        when(repository.findIssueByTitle(testIssue.getTitle())).thenReturn(new Issue());
        assertThrows(TitleAlreadyExistsException.class,
                () -> service.saveIssueToDatabase(testIssue));
    }

    @Test
    public void findIssueById_givenAllOk_shouldCallRepository() {
        when(repository.findIssueById(1)).thenReturn(testIssue);
        service.findIssueById(1);
        verify(repository, times(1)).findIssueById(1);
    }

    @Test
    public void findIssueById_givenAllOk_shouldReturnResults() {
        when(repository.findIssueById(1)).thenReturn(testIssue);
        assertEquals(testIssue, service.findIssueById(1));
    }

    @Test
    public void findIssueByTitle_givenAllOk_shouldCallRepository() {
        when(repository.findIssueByTitle(GENERAL_TEST_TITLE)).thenReturn(testIssue);
        service.findIssueByTitle(GENERAL_TEST_TITLE);
        verify(repository, times(1)).findIssueByTitle(GENERAL_TEST_TITLE);
    }

    @Test
    public void findIssueByTitle_givenAllOk_shouldReturnResults() {
        when(repository.findIssueByTitle(GENERAL_TEST_TITLE)).thenReturn(testIssue);
        assertEquals(testIssue, service.findIssueByTitle(GENERAL_TEST_TITLE));
    }

    @Test
    public void findAllIssues_givenAllOk_shouldCallRepository() {
        when(repository.findAll()).thenReturn(new LinkedList<>());
        service.findAllIssues();
        verify(repository, times(1)).findAll();
    }

    @Test
    public void findAllIssues_givenAllOk_shouldReturnResults() {
        when(repository.findAll()).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssues().contains(testIssue));
    }

    @Test
    public void findAllIssuesByType_givenAllOk_shouldCallRepository() {
        when(repository.findAllIssuesByType(IssueType.BUG)).thenReturn(new LinkedList<>());
        service.findAllIssuesByType(IssueType.BUG);
        verify(repository, times(1)).findAllIssuesByType(IssueType.BUG);
    }

    @Test
    public void findAllIssuesByType_givenAllOk_shouldReturnResults() {
        when(repository.findAllIssuesByType(IssueType.BUG)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByType(IssueType.BUG).contains(testIssue));
    }

    @Test
    public void findAllIssuesBySeverity_givenAllOk_shouldCallRepository() {
        when(repository.findAllIssuesBySeverity(Severity.HIGH)).thenReturn(new LinkedList<>());
        service.findAllIssuesBySeverity(Severity.HIGH);
        verify(repository, times(1)).findAllIssuesBySeverity(Severity.HIGH);
    }

    @Test
    public void findAllIssuesBySeverity_givenAllOk_shouldReturnResults() {
        when(repository.findAllIssuesBySeverity(Severity.HIGH)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesBySeverity(Severity.HIGH).contains(testIssue));
    }

    @Test
    public void findAllIssuesByStatus_givenAllOk_shouldCallRepository() {
        when(repository.findAllIssuesByStatus(Status.SUBMITTED)).thenReturn(new LinkedList<>());
        service.findAllIssuesByStatus(Status.SUBMITTED);
        verify(repository, times(1)).findAllIssuesByStatus(Status.SUBMITTED);
    }

    @Test
    public void findAllIssuesByStatus_givenAllOk_shouldReturnResults() {
        when(repository.findAllIssuesByStatus(Status.SUBMITTED)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByStatus(Status.SUBMITTED).contains(testIssue));
    }

    @Test
    public void findAllIssuesByProject_givenAllOk_shouldCallRepository() {
        when(repository.findAllIssuesByProject(Project.MWP)).thenReturn(new LinkedList<>());
        service.findAllIssuesByProject(Project.MWP);
        verify(repository, times(1)).findAllIssuesByProject(Project.MWP);
    }

    @Test
    public void findAllIssuesByProject_givenAllOk_shouldReturnResults() {
        when(repository.findAllIssuesByProject(Project.MWP)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByProject(Project.MWP).contains(testIssue));
    }

    @Test
    public void updateIssueWithNewProperties_givenAllOk_shouldCallRepository() {
        service.updateIssueWithNewProperties(updateTestIssue);
        verify(repository, times(1)).updateIssueWithNewType(1, IssueType.ENHANCEMENT);
        verify(repository, times(1)).updateIssueWithNewSeverity(1, Severity.HIGH);
        verify(repository, times(1)).updateIssueWithNewStatus(1, Status.SOLVED);
        verify(repository, times(1)).updateIssueWithNewProject(1, Project.TRACKER);
    }

    @Test
    public void updateIssueWithNewDescription_givenAllOk_shouldCallRepository() {
        service.updateIssueWithNewDescription(descriptionDto);
        verify(repository, times(1)).updateIssueWithNewDescription(NEW_TITLE, NEW_DESCRIPTION);
    }


    @Test
    public void filterIssues_givenAllOk_shouldCallRepository() {
        when(repository.findAll()).thenReturn(findAllResults);
        service.filterIssues(testFilterDto);
        verify(repository, times(1)).findAllIssuesByType(IssueType.BUG);
        verify(repository, times(0)).findAllIssuesBySeverity(Severity.HIGH);
        verify(repository, times(1)).findAllIssuesByStatus(Status.SUBMITTED);
        verify(repository, times(1)).findAllIssuesByProject(Project.MWP);
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
        when(repository.findAllIssuesByType(IssueType.BUG)).thenReturn(findAllBugs);
        when(repository.findAllIssuesByStatus(Status.SUBMITTED)).thenReturn(findAllSubmitted);
        when(repository.findAllIssuesByProject(Project.MWP)).thenReturn(findAllMWP);
        assertEquals(Collections.singletonList(testIssue), service.filterIssues(testFilterDto));
    }

    @Test
    public void deleteIssueById_shouldCallRepository() {
        service.deleteIssueById(1);
        verify(repository, times(1)).deleteById(1);
    }

    private List<Issue> getFindAllResults() {
        List<Issue> findAllResults = new LinkedList<>();
        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);
        return findAllResults;
    }


}