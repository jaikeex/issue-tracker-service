package com.jaikeex.issuetrackerservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.HistoryRecord;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.HistoryRepository;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import com.jaikeex.issuetrackerservice.utility.htmlparser.HtmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class HistoryServiceTest {

    private static final String TEST_RECORD_TEXT = "test text";
    private static final int TEST_RECORD_ID = 1;
    private static final int TEST_ISSUE_ID = 10;
    @Mock
    HistoryRepository repository;

    @InjectMocks
    HistoryService service;

    HistoryRecord testRecord;
    Issue testIssue;

    @BeforeEach
    public void beforeEach() {
        testIssue = new Issue();
        testRecord = new HistoryRecord();
        testRecord.setId(TEST_RECORD_ID);
        testRecord.setText(TEST_RECORD_TEXT);
        testRecord.setIssue(testIssue);
    }

    @Test
    public void saveNewRecord_shouldCallRepository() {
        service.saveNewRecord(testRecord);
        verify(repository, times(1)).save(testRecord);
    }

    @Test
    public void findAllRecordsByIssueId_givenValidIssueId_shouldReturnCorrectResults() {
        List<HistoryRecord> expectedResults = new LinkedList<>();
        expectedResults.add(testRecord);
        when(repository.findByIssueId(TEST_ISSUE_ID))
                .thenReturn(Collections.singletonList(testRecord));
        assertEquals(expectedResults, service.findByIssueId(TEST_ISSUE_ID));
        verify(repository, times(1))
                .findByIssueId(TEST_ISSUE_ID);
    }
}