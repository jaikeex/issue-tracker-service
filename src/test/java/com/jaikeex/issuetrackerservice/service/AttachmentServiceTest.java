package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.config.properties.StorageProperties;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentServiceImpl;
import com.jaikeex.issuetrackerservice.service.history.HistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AttachmentServiceTest {

    private static final String TEST_ATTACHMENTS_FOLDER = "testAttachmentsFolder";
    @Mock
    IssueRepository issueRepository;
    @Mock
    AttachmentRepository attachmentRepository;
    @MockBean
    StorageProperties properties;
    @Mock
    HistoryServiceImpl historyService;

    AttachmentServiceImpl attachmentService;


    @BeforeEach
    public void beforeEach() {
        when(properties.getIssueAttachmentsFolder()).thenReturn("testIssueFolder");
        when(properties.getAttachmentDownloadEndpoint()).thenReturn("testIssueEndpoint");
        attachmentService = new AttachmentServiceImpl(issueRepository, attachmentRepository, historyService, properties);
    }

    @Test
    public void saveAttachment_shouldCallRepository() {

    }
}