package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/issue/attachments")
@Slf4j
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadNewAttachment(
            @RequestBody AttachmentFileDto attachmentFileDto) throws IOException {
        attachmentService.saveAttachment(attachmentFileDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{issueId}/{filename}")
    public ResponseEntity<Object> downloadAttachmentByFilenameAndIssueId(
            @PathVariable String filename,
            @PathVariable String issueId,
            HttpServletResponse response) throws IOException {
        attachmentService.downloadAttachment(filename, issueId, response);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAttachmentById(@PathVariable int id) throws IOException {
        attachmentService.deleteAttachmentById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleAttachmentServiceException(IOException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}




