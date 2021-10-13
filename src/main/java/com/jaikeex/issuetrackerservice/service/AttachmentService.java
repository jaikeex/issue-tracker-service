package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.config.storage.StorageProperties;
import com.jaikeex.issuetrackerservice.dto.AttachmentBytesDto;
import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AttachmentService {

    private final IssueRepository issueRepository;
    private final AttachmentRepository attachmentRepository;
    private final Path issueAttachmentsFolder;
    private final String issueVolumeFolder;

    @Autowired
    public AttachmentService(IssueRepository issueRepository,
                             AttachmentRepository attachmentRepository,
                             StorageProperties properties) {
        this.issueRepository = issueRepository;
        this.attachmentRepository = attachmentRepository;
        this.issueAttachmentsFolder = Paths.get(properties.getIssueAttachmentsFolder());
        this.issueVolumeFolder = properties.getIssueVolumeFolder();
    }

    public Issue save(AttachmentBytesDto attachmentBytesDto) throws IOException {
        Issue issue = issueRepository.findIssueByTitle(attachmentBytesDto.getTitle());
        int issueId = issue.getId();

        if (attachmentBytesDto.getAttachmentBytes().length == 0) {
            //TODO: Make a custom exception for this
            throw new RuntimeException("REXX");
        }

        Path dirPath = Paths.get(issueAttachmentsFolder.toString() + "/" + issueId);
        String filePath = issueId + "/" + attachmentBytesDto.getOriginalFilename();
        Files.createDirectories(dirPath);
        System.out.println(dirPath);
        Path destinationPath = issueAttachmentsFolder.resolve(
                Paths.get(filePath)).normalize().toAbsolutePath();
        Files.write(destinationPath, attachmentBytesDto.getAttachmentBytes());

        Attachment newAttachment = new Attachment(issueVolumeFolder + filePath, issue);
        attachmentRepository.save(newAttachment);

        return issueRepository.findIssueById(issueId);
    }
}
