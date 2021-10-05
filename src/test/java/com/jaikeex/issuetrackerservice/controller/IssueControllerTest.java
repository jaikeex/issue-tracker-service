package com.jaikeex.issuetrackerservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.service.FilterService;
import com.jaikeex.issuetrackerservice.service.IssueService;
import com.jaikeex.issuetrackerservice.service.SearchService;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(IssueController.class)
class IssueControllerTest {

    private static final String CROSS_ORIGIN_VALUE = "*";
    private static final String CROSS_ORIGIN_ALLOWED_METHODS = "POST, PUT, GET, OPTIONS, DELETE";
    private static final String CROSS_ORIGIN_ALLOWED_HEADERS = "Authorization, Content-Type";
    private static final String CROSS_ORIGIN_MAX_AGE = "3600";
    private static final String GENERAL_TEST_ISSUE_DESCRIPTION = "this is the general test ISSUE";
    private static final String UPDATE_TEST_ISSUE_DESCRIPTION = "this is the update test issue";
    private static final String GENERAL_TEST_AUTHOR = "general author";
    private static final String UPDATE_TEST_AUTHOR = "update author";
    private static final String GENERAL_TEST_TITLE = "testTitle";
    private static final String UPDATE_TEST_TITLE = "update title";
    private static final String NEW_DESCRIPTION = "new description";
    private static final String NEW_TITLE = "new title";

    @MockBean
    IssueService service;
    @MockBean
    SearchService searchService;
    @MockBean
    FilterService filterService;

    @Autowired
    private MockMvc mockMvc;


    Issue testIssue;
    Issue updateTestIssue;
    String testIssueJson;
    String updateTestIssueJson;

    FilterDto testFilterDto;
    String testFilterDtoJson;
    DescriptionDto descriptionDto;
    String descriptionDtoJson;

    @BeforeEach
    public void beforeEach() throws JsonProcessingException {
        initTestIssue();
        initUpdateTestIssue();
        initFilterDto();
        initDescriptionDto();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        testIssueJson = writer.writeValueAsString(testIssue);
        updateTestIssueJson = writer.writeValueAsString(updateTestIssue);
        testFilterDtoJson = writer.writeValueAsString(testFilterDto);
        descriptionDtoJson = writer.writeValueAsString(descriptionDto);
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

    private void initFilterDto() {
        testFilterDto = new FilterDto();
        testFilterDto.setType(IssueType.BUG);
        testFilterDto.setSeverity(Severity.CRITICAL);
        testFilterDto.setStatus(Status.SUBMITTED);
        testFilterDto.setProject(Project.MWP);
    }

    private void initDescriptionDto() {
        descriptionDto = new DescriptionDto();
        descriptionDto.setDescription(NEW_DESCRIPTION);
        descriptionDto.setTitle(NEW_TITLE);
    }


    @Test
    public void createNewIssue_shouldCallIssueService() throws Exception {
        mockMvc.perform(post("/issue/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIssueJson));
        verify(service, times(1)).saveIssueToDatabase(testIssue);
    }

    @Test
    public void createNewIssue_givenValidData_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/issue/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIssueJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void createNewIssue_shouldCatchTitleAlreadyExistsExceptionAndReturnConflict() throws Exception {
        when(service.saveIssueToDatabase(testIssue))
                .thenThrow(TitleAlreadyExistsException.class);
        mockMvc.perform(post("/issue/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIssueJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void createNewIssue_shouldSendCorsHeaders() throws Exception {
        assertCorsHeadersAreReturned(mockMvc.perform(post("/issue/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIssueJson)));
    }

    @Test
    public void deleteIssueById_shouldCallIssueService() throws Exception {
        mockMvc.perform(delete("/issue/id/1"));
        verify(service, times(1)).deleteIssueById(1);
    }

    @Test
    public void deleteIssueById_givenValidId_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/issue/id/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteIssueById_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/issue/id/1"));
        assertCorsHeadersAreReturned(resultActions);
    }


    @Test
    public void findIssueById_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/id/1"));
        verify(service, times(1)).findIssueById(1);
    }

    @Test
    public void findIssueById_givenValidId_shouldReturnOk() throws Exception {
        when(service.findIssueById(1)).thenReturn(testIssue);
        mockMvc.perform(get("/issue/id/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void findIssueById_givenNotFound_shouldReturnNotFound() throws Exception {
        when(service.findIssueById(1)).thenReturn(null);
        mockMvc.perform(get("/issue/id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findIssueById_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/id/1"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void findIssueByTitle_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/title/testTitle"));
        verify(service, times(1)).findIssueByTitle("testTitle");
    }

    @Test
    public void findIssueByTitle_givenValidId_shouldReturnOk() throws Exception {
        when(service.findIssueByTitle("testTitle")).thenReturn(testIssue);
        mockMvc.perform(get("/issue/title/testTitle"))
                .andExpect(status().isOk());
    }

    @Test
    public void findIssueByTitle_givenNotFound_shouldReturnNotFound() throws Exception {
        when(service.findIssueByTitle("testTitle")).thenReturn(null);
        mockMvc.perform(get("/issue/title/testTitle"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findIssueByTitle_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/title/testTitle"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void findAllIssues_shouldReturnOk() throws Exception {
        when(service.findIssueByTitle("testTitle")).thenReturn(null);
        mockMvc.perform(get("/issue/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllIssues_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/all"));
        verify(service, times(1)).findAllIssues();
    }

    @Test
    public void findAllIssues_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/all"));
        assertCorsHeadersAreReturned(resultActions);
    }
    @Test
    public void findAllByType_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/type/BUG"));
        verify(service, times(1)).findAllIssuesByType(IssueType.BUG);
    }

    @Test
    public void findAllByType_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/type/BUG"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void findAllBySeverity_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/severity/CRITICAL"));
        verify(service, times(1)).findAllIssuesBySeverity(Severity.CRITICAL);
    }

    @Test
    public void findAllBySeverity_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/severity/CRITICAL"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void findAllByStatus_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/status/OPEN"));
        verify(service, times(1)).findAllIssuesByStatus(Status.OPEN);
    }

    @Test
    public void findAllByStatus_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/status/OPEN"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void findAllByProject_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/project/MWP"));
        verify(service, times(1)).findAllIssuesByProject(Project.MWP);
    }


    @Test
    public void findAllByProject_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/project/MWP"));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void updateIssueWithNewProperties_shouldCallIssueService() throws Exception {
        mockMvc.perform(put("/issue/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateTestIssueJson));
        verify(service, times(1)).updateIssueWithNewProperties(updateTestIssue);
    }

    @Test
    public void updateIssueWithNewProperties_givenAllOk_shouldCallReturnOk() throws Exception {
        mockMvc.perform(put("/issue/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateTestIssueJson))
                .andExpect(status().isOk());
    }

    @Test
    public void updateIssueWithNewDescription_shouldCallIssueService() throws Exception {
        mockMvc.perform(post("/issue/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(descriptionDtoJson));
        verify(service, times(1)).updateIssueWithNewDescription(descriptionDto);
    }

    @Test
    public void updateIssueWithNewDescription_givenAllOk_shouldCallReturnOk() throws Exception {
        mockMvc.perform(post("/issue/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(descriptionDtoJson))
                .andExpect(status().isOk());
    }

    @Test
    public void updateIssueWithNewDescription_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/issue/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(descriptionDtoJson));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void filterIssues_shouldCallIssueService() throws Exception {
        mockMvc.perform(post("/issue/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFilterDtoJson));
        verify(filterService, times(1)).filterIssues(testFilterDto);
    }

    @Test
    public void filterIssues_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/issue/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFilterDtoJson));
        assertCorsHeadersAreReturned(resultActions);
    }

    @Test
    public void searchIssues_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/search?query=testQuery"));
        verify(searchService, times(1)).searchIssues("testQuery");
    }

    @Test
    public void searchIssues_givenNoParamDeclaration_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/search"));
        verify(searchService, times(1)).searchIssues(null);
    }

    @Test
    public void searchIssues_givenNoParamValue_shouldCallIssueService() throws Exception {
        mockMvc.perform(get("/issue/search?query="));
        verify(searchService, times(1)).searchIssues("");
    }

    @Test
    public void searchIssues_shouldSendCorsHeaders() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/issue/search?query=testQuery"));
        assertCorsHeadersAreReturned(resultActions);

    }

    private void assertCorsHeadersAreReturned(ResultActions resultActions) throws Exception {
        resultActions.andExpect(header().string("Access-Control-Allow-Origin", CROSS_ORIGIN_VALUE))
                .andExpect(header().string("Access-Control-Allow-Methods", CROSS_ORIGIN_ALLOWED_METHODS))
                .andExpect(header().string("Access-Control-Allow-Headers", CROSS_ORIGIN_ALLOWED_HEADERS))
                .andExpect(header().string("Access-Control-Max-Age", CROSS_ORIGIN_MAX_AGE));
    }

}


