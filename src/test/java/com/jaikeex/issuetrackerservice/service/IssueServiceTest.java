package com.jaikeex.issuetrackerservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentServiceImpl;
import com.jaikeex.issuetrackerservice.service.history.HistoryServiceImpl;
import com.jaikeex.issuetrackerservice.service.issue.IssueServiceImpl;
import com.jaikeex.issuetrackerservice.utility.exception.TitleAlreadyExistsException;
import com.jaikeex.issuetrackerservice.utility.html.HtmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private static final byte[] TEST_BYTES = {1, 2, 3};
    private static final String TEST_ORIGINAL_FILENAME = "testOriginalFilename";
    private static final int TEST_ID = 1;
    @Mock
    IssueRepository repository;
    @Mock
    HistoryServiceImpl historyService;
    @Mock
    AttachmentServiceImpl attachmentService;
    @Mock
    HtmlParser parser;

    @InjectMocks
    IssueServiceImpl service;

    Issue testIssue;
    Issue updateTestIssue;
    IssueDto updateTestIssueDto;
    Issue filterTestIssue;
    IssueDto descriptionDto;
    IssueDto testIssueDto;
    AttachmentFileDto testAttachmentFileDto;

    List<Issue> findAllResults = new LinkedList<>();

    @BeforeEach
    public void beforeEach(){
        initTestIssue();
        initUpdateTestIssue();
        initUpdateTestIssueDto();
        initFilterTestIssue();
        initDescriptionDto();
        initAttachmentFileDto();
        initTestIssueDto();

        findAllResults.add(testIssue);
        findAllResults.add(updateTestIssue);
        findAllResults.add(filterTestIssue);
    }

    private void initDescriptionDto() {
        descriptionDto = new IssueDto();
        descriptionDto.setDescription(NEW_DESCRIPTION);
        descriptionDto.setTitle(NEW_TITLE);
    }

    private void initAttachmentFileDto() {
        testAttachmentFileDto = new AttachmentFileDto();
        testAttachmentFileDto.setIssueTitle(GENERAL_TEST_TITLE);
        testAttachmentFileDto.setBytes(TEST_BYTES);
        testAttachmentFileDto.setOriginalFilename(TEST_ORIGINAL_FILENAME);
    }

    private void initTestIssue() {
        testIssue = new Issue();
        testIssue.setId(TEST_ID);
        testIssue.setTitle(GENERAL_TEST_TITLE);
        testIssue.setDescription(GENERAL_TEST_ISSUE_DESCRIPTION);
        testIssue.setAuthor(GENERAL_TEST_AUTHOR);
        testIssue.setType(IssueType.BUG);
        testIssue.setSeverity(Severity.CRITICAL);
        testIssue.setStatus(Status.SUBMITTED);
        testIssue.setProject(Project.MWP);
    }

    private void initTestIssueDto() {
        testIssueDto = new IssueDto();
        testIssueDto.setTitle(GENERAL_TEST_TITLE);
        testIssueDto.setDescription(GENERAL_TEST_ISSUE_DESCRIPTION);
        testIssueDto.setAuthor(GENERAL_TEST_AUTHOR);
        testIssueDto.setType(IssueType.BUG);
        testIssueDto.setSeverity(Severity.CRITICAL);
        testIssueDto.setProject(Project.MWP);
        testIssueDto.setAttachmentFileDto(testAttachmentFileDto);
    }

    private void initUpdateTestIssueDto() {
        updateTestIssueDto = new IssueDto();
        updateTestIssueDto.setId(TEST_ID);
        updateTestIssueDto.setTitle(UPDATE_TEST_TITLE);
        updateTestIssueDto.setDescription(UPDATE_TEST_ISSUE_DESCRIPTION);
        updateTestIssueDto.setAuthor(UPDATE_TEST_AUTHOR);
        updateTestIssueDto.setType(IssueType.ENHANCEMENT);
        updateTestIssueDto.setSeverity(Severity.HIGH);
        updateTestIssueDto.setStatus(Status.SOLVED);
        updateTestIssueDto.setProject(Project.TRACKER);
    }

    private void initUpdateTestIssue() {
        updateTestIssue = new Issue();
        updateTestIssue.setId(TEST_ID);
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
        filterTestIssue.setId(TEST_ID);
        filterTestIssue.setTitle(FILTER_TEST_TITLE);
        filterTestIssue.setDescription(FILTER_TEST_ISSUE_DESCRIPTION);
        filterTestIssue.setAuthor(FILTER_TEST_AUTHOR);
        filterTestIssue.setType(IssueType.BUG);
        filterTestIssue.setSeverity(Severity.HIGH);
        filterTestIssue.setStatus(Status.SOLVED);
        filterTestIssue.setProject(Project.MWP);
    }

    @Test
    public void saveIssueToDatabase_givenAllOk_shouldCallRepository() throws IOException {
        Issue issue = new Issue(testIssueDto);
        when(repository.findIssueByTitle(issue.getTitle())).thenReturn(null);
        service.saveNewIssue(testIssueDto);
        verify(repository, times(1)).save(issue);
    }

    @Test
    public void saveIssueToDatabase_givenTitleAlreadyExists_shouldThrowException() {
        when(repository.findIssueByTitle(testIssue.getTitle())).thenReturn(new Issue());
        assertThrows(TitleAlreadyExistsException.class,
                () -> service.saveNewIssue(testIssueDto));
    }

    @Test
    public void findIssueById_givenAllOk_shouldCallRepository() {
        when(repository.findById(TEST_ID)).thenReturn(Optional.ofNullable(testIssue));
        service.findIssueById(TEST_ID);
        verify(repository, times(1)).findById(TEST_ID);
    }

    @Test
    public void findIssueById_givenAllOk_shouldReturnResults() {
        when(repository.findById(TEST_ID)).thenReturn(Optional.ofNullable(testIssue));
        assertEquals(testIssue, service.findIssueById(TEST_ID));
    }

    @Test
    public void findIssueByTitle_givenAllOk_shouldCallRepository() {
        when(repository.findByTitle(GENERAL_TEST_TITLE)).thenReturn(Optional.ofNullable(testIssue));
        service.findIssueByTitle(GENERAL_TEST_TITLE);
        verify(repository, times(1)).findByTitle(GENERAL_TEST_TITLE);
    }

    @Test
    public void findIssueByTitle_givenAllOk_shouldReturnResults() {
        when(repository.findByTitle(GENERAL_TEST_TITLE)).thenReturn(Optional.ofNullable(testIssue));
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
        when(repository.findAllByType(IssueType.BUG)).thenReturn(new LinkedList<>());
        service.findAllIssuesByType(IssueType.BUG);
        verify(repository, times(1)).findAllByType(IssueType.BUG);
    }

    @Test
    public void findAllIssuesByType_givenAllOk_shouldReturnResults() {
        when(repository.findAllByType(IssueType.BUG)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByType(IssueType.BUG).contains(testIssue));
    }

    @Test
    public void findAllIssuesBySeverity_givenAllOk_shouldCallRepository() {
        when(repository.findAllBySeverity(Severity.HIGH)).thenReturn(new LinkedList<>());
        service.findAllIssuesBySeverity(Severity.HIGH);
        verify(repository, times(1)).findAllBySeverity(Severity.HIGH);
    }

    @Test
    public void findAllIssuesBySeverity_givenAllOk_shouldReturnResults() {
        when(repository.findAllBySeverity(Severity.HIGH)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesBySeverity(Severity.HIGH).contains(testIssue));
    }

    @Test
    public void findAllIssuesByStatus_givenAllOk_shouldCallRepository() {
        when(repository.findAllByStatus(Status.SUBMITTED)).thenReturn(new LinkedList<>());
        service.findAllIssuesByStatus(Status.SUBMITTED);
        verify(repository, times(1)).findAllByStatus(Status.SUBMITTED);
    }

    @Test
    public void findAllIssuesByStatus_givenAllOk_shouldReturnResults() {
        when(repository.findAllByStatus(Status.SUBMITTED)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByStatus(Status.SUBMITTED).contains(testIssue));
    }

    @Test
    public void findAllIssuesByProject_givenAllOk_shouldCallRepository() {
        when(repository.findAllByProject(Project.MWP)).thenReturn(new LinkedList<>());
        service.findAllIssuesByProject(Project.MWP);
        verify(repository, times(1)).findAllByProject(Project.MWP);
    }

    @Test
    public void findAllIssuesByProject_givenAllOk_shouldReturnResults() {
        when(repository.findAllByProject(Project.MWP)).thenReturn(Collections.singletonList(testIssue));
        assertTrue(service.findAllIssuesByProject(Project.MWP).contains(testIssue));
    }

    @Test
    public void updateIssueWithNewProperties_givenAllOk_shouldCallRepository() {
        when(repository.findById(TEST_ID)).thenReturn(Optional.ofNullable(updateTestIssue));
        service.updateIssueWithNewProperties(updateTestIssueDto);
        verify(repository, times(1)).updateIssueWithNewType(TEST_ID, IssueType.ENHANCEMENT);
        verify(repository, times(1)).updateIssueWithNewSeverity(TEST_ID, Severity.HIGH);
        verify(repository, times(1)).updateIssueWithNewStatus(TEST_ID, Status.SOLVED);
        verify(repository, times(1)).updateIssueWithNewProject(TEST_ID, Project.TRACKER);
    }

    @Test
    public void updateIssueWithNewDescription_givenAllOk_shouldCallRepository() {
        when(repository.findByTitle(NEW_TITLE)).thenReturn(Optional.ofNullable(testIssue));
        service.updateIssueWithNewDescription(descriptionDto);
        verify(repository, times(1)).updateIssueWithNewDescription(NEW_TITLE, NEW_DESCRIPTION);
    }

    @Test
    public void deleteIssueById_shouldCallRepository() {
        service.deleteIssueById(1);
        verify(repository, times(1)).deleteById(1);
    }



}