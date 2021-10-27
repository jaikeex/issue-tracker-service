package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.config.storage.StorageProperties;
import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import com.jaikeex.issuetrackerservice.utility.exceptions.EmptyAttachmentFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AttachmentService {

    private final IssueRepository issueRepository;
    private final AttachmentRepository attachmentRepository;
    private final HistoryService historyService;

    private final Path issueAttachmentsFolder;
    private final String attachmentDownloadEndpoint;

    @Autowired
    public AttachmentService(IssueRepository issueRepository,
                             AttachmentRepository attachmentRepository,
                             StorageProperties storageProperties,
                             HistoryService historyService) {
        this.issueRepository = issueRepository;
        this.attachmentRepository = attachmentRepository;
        this.historyService = historyService;

        this.issueAttachmentsFolder = Paths.get(storageProperties.getIssueAttachmentsFolder());
        this.attachmentDownloadEndpoint = storageProperties.getAttachmentDownloadEndpoint();
    }

    /**Saves the attachment file on disk and creates its record in database.
     * @param attachmentFileDto Data transfer object which contains the file
     *                          itself and other data needed to process
     *                          and save the attachment.
     * @return Issue object which the attachment belongs to
     *         (loaded after the upload completes).
     * @throws IOException when there is a problem with either saving the file
     *                     or creating the issue-specific folder.
     */
    public Issue saveAttachment(AttachmentFileDto attachmentFileDto) throws IOException {
        Issue issue = issueRepository.findIssueByTitle(attachmentFileDto.getIssueTitle());
        String filePath = issue.getId() + "/" + attachmentFileDto.getOriginalFilename();
        String downloadLink = attachmentDownloadEndpoint + filePath;
        setupTheProcess(attachmentFileDto, issue);
        writeAttachmentFileToDisk(attachmentFileDto.getBytes(), filePath);
        saveAttachmentReferenceInDatabase(attachmentFileDto, issue, downloadLink);
        log.info("Successfully saved new attachment file [filename={}]", attachmentFileDto.getOriginalFilename());
        return issueRepository.findIssueById(issue.getId());
    }

    /**Deletes both the attachment file and its database record.
     * @param id Database id of the attachment that is about to be deleted.
     * @throws IOException when there is a problem with deleting the
     *                     attachment file from disk.
     */
    public void deleteAttachment(int id) throws IOException {
        Attachment attachmentToDelete = attachmentRepository.findAttachmentById(id);
        Issue parentIssue = attachmentToDelete.getIssue();
        String attachmentFilePath = parentIssue.getId() + "/" + attachmentToDelete.getOriginalFilename();
        deleteAttachmentFileFromDisk(attachmentFilePath);
        deleteAttachmentReferenceFromDatabase(id, attachmentToDelete, parentIssue);
        log.info("Successfully deleted an attachment file [filename={}]", attachmentToDelete.getOriginalFilename());
    }

    /**Copies the file into the provided http response.
     * @param filename name of the file.
     * @param issueId database id of the issue which the file belongs to.
     * @param response used to send the requested file to user.
     * @throws IOException when there is a problem with downloading the file.
     */
    public void downloadAttachment(String filename, String issueId, HttpServletResponse response) throws IOException {
        InputStream inputStream = getAttachmentFileInputStream(filename, issueId);
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
        log.info("Processed an attachment file for download [filename={}]", filename);
    }

    private InputStream getAttachmentFileInputStream(String filename, String issueId) throws FileNotFoundException {
        File file = new File(String.format("/issue/attachments/%s/%s", issueId, filename));
        return new FileInputStream(file);
    }

    private void deleteAttachmentReferenceFromDatabase(int issueId, Attachment attachmentToDelete, Issue issue) {
        historyService.record(RecordType.DELETE_ATTACHMENT, issue, attachmentToDelete);
        attachmentRepository.deleteById(issueId);
    }

    private void deleteAttachmentFileFromDisk(String filePath) throws IOException {
        Path destinationPath = issueAttachmentsFolder.resolve(
                Paths.get(filePath)).normalize().toAbsolutePath();
        Files.deleteIfExists(destinationPath);
    }

    private void setupTheProcess(AttachmentFileDto attachmentFileDto, Issue issue)
            throws IOException {
        checkForEmptyFile(attachmentFileDto);
        createIssueAttachmentDirectory(issue.getId());
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
}
