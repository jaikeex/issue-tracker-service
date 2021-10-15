package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.config.storage.StorageProperties;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AttachmentServiceTest {

    private static final String TEST_ATTACHMENTS_FOLDER = "testAttachmentsFolder";
    @Mock
    IssueRepository issueRepository;
    @Mock
    AttachmentRepository attachmentRepository;
    @Mock
    StorageProperties properties;
    @Mock
    HistoryService historyService;

    AttachmentService attachmentService;


    @BeforeEach
    public void beforeEach() {
        when(properties.getIssueAttachmentsFolder()).thenReturn("testIssueFolder");
        when(properties.getAttachmentDownloadEndpoint()).thenReturn("testIssueEndpoint");
        attachmentService = new AttachmentService(issueRepository, attachmentRepository, properties, historyService);
    }

    @Test
    public void saveAttachment_shouldCallRepository() {

    }
}