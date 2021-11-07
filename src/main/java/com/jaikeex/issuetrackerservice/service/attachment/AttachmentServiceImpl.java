package com.jaikeex.issuetrackerservice.service.attachment;

import com.jaikeex.issuetrackerservice.config.storage.StorageProperties;
import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.history.HistoryService;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import com.jaikeex.issuetrackerservice.utility.exception.EmptyAttachmentFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private static final String CACHE_NAME = "issue-cache-eh";
    private static final String ISSUE_NOT_FOUND_EXCEPTION_MESSAGE = "Requested issue report does not exist in the database.";
    private static final String ATTACHMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Requested file does not exist.";

    private final IssueRepository issueRepository;
    private final AttachmentRepository attachmentRepository;
    private final HistoryService historyService;

    private Path issueAttachmentsFolder;
    private String attachmentDownloadEndpoint;

    @Autowired
    public AttachmentServiceImpl(IssueRepository issueRepository,
                                 AttachmentRepository attachmentRepository,
                                 HistoryService historyService) {
        this.issueRepository = issueRepository;
        this.attachmentRepository = attachmentRepository;
        this.historyService = historyService;
    }

    @Autowired
    public void setIssueAttachmentsFolder(StorageProperties storageProperties) {
        this.issueAttachmentsFolder = Paths.get(storageProperties.getIssueAttachmentsFolder());
    }

    @Autowired
    public void setAttachmentDownloadEndpoint(StorageProperties storageProperties) {
        this.attachmentDownloadEndpoint = storageProperties.getAttachmentDownloadEndpoint();
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void saveAttachment(AttachmentFileDto attachmentFileDto) throws IOException {
        Optional<Issue> issue = issueRepository.findByTitle(attachmentFileDto.getIssueTitle());
        if (issue.isPresent()) {
            saveAttachmentToDiskAndDatabase(attachmentFileDto, issue.get());
        }
        else {
            throw new EntityNotFoundException(ISSUE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteAttachmentById(int id) throws IOException {
        Optional<Attachment> attachmentToDelete = attachmentRepository.findById(id);
        if(attachmentToDelete.isPresent()) {
            deleteAttachmentFromDiskAndDatabase(attachmentToDelete.get());
        }
        else {
            throw new EntityNotFoundException(ATTACHMENT_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void downloadAttachment(String filename,
                                   String issueId,
                                   HttpServletResponse response) throws IOException {
        copyFileToResponse(filename, issueId, response);
        response.flushBuffer();
    }

    private void saveAttachmentToDiskAndDatabase(AttachmentFileDto attachmentFileDto, Issue issue) throws IOException {
        String filePath = getFilePath(issue, attachmentFileDto.getOriginalFilename());
        String downloadLink = getDownloadLink(filePath);
        checkForEmptyFile(attachmentFileDto);
        createIssueAttachmentDirectory(issue.getId());
        writeAttachmentFileToDisk(attachmentFileDto.getBytes(), filePath);
        saveAttachmentReferenceInDatabase(attachmentFileDto, issue, downloadLink);
        log.info("Successfully saved new attachment file [filename={}]", attachmentFileDto.getOriginalFilename());
    }

    private void deleteAttachmentFromDiskAndDatabase(Attachment attachmentToDelete) throws IOException {
        Issue parentIssue = attachmentToDelete.getIssue();
        String attachmentFilePath = getFilePath(parentIssue, attachmentToDelete.getOriginalFilename());
        deleteAttachmentFileFromDisk(attachmentFilePath);
        deleteAttachmentFromDatabase(attachmentToDelete, parentIssue);
        log.info("Successfully deleted an attachment file [filename={}]", attachmentToDelete);
    }

    private void copyFileToResponse(String filename, String issueId, HttpServletResponse response) throws IOException {
        File file = new File(String.format("/issue/attachments/%s/%s", issueId, filename));
        InputStream inputStream = new FileInputStream(file);
        IOUtils.copy(inputStream, response.getOutputStream());
        log.info("Processed an attachment file for download [filename={}]", filename);
    }

    private void deleteAttachmentFromDatabase(Attachment attachmentToDelete, Issue issue) {
        historyService.record(RecordType.DELETE_ATTACHMENT, issue, attachmentToDelete);
        attachmentRepository.deleteById(attachmentToDelete.getId());
    }

    private void deleteAttachmentFileFromDisk(String filePath) throws IOException {
        Path destinationPath = issueAttachmentsFolder.resolve(
                Paths.get(filePath)).normalize().toAbsolutePath();
        Files.deleteIfExists(destinationPath);
    }

    private void saveAttachmentReferenceInDatabase(
            AttachmentFileDto attachmentFileDto,
            Issue issue,
            String downloadLink) {
        Attachment newAttachment = new Attachment(downloadLink, attachmentFileDto, issue);
        historyService.record(RecordType.ADD_ATTACHMENT, issue, newAttachment);
        attachmentRepository.save(newAttachment);
    }

    private void writeAttachmentFileToDisk(byte[] bytes, String filePath) throws IOException {
        Path destinationPath = issueAttachmentsFolder.resolve(
                Paths.get(filePath)).normalize().toAbsolutePath();
        Files.write(destinationPath, bytes);
    }

    private void checkForEmptyFile(AttachmentFileDto attachmentFileDto) {
        if (attachmentFileDto.getBytes().length == 0) {
            throw new EmptyAttachmentFileException();
        }
    }

    private void createIssueAttachmentDirectory(int issueId) throws IOException {
        Path issueDirectoryPath = Paths.get(
                issueAttachmentsFolder.toString() + "/" + issueId);
        Files.createDirectories(issueDirectoryPath);
    }

    private String getFilePath(Issue issue, String originalFilename) {
        return issue.getId() + "/" + originalFilename;
    }

    private String getDownloadLink(String filePath) {
        return attachmentDownloadEndpoint + filePath;
    }
}
