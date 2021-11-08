package com.jaikeex.issuetrackerservice.service.attachment;

import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Manages the attachments of individual issue reports.
 */
public interface AttachmentService {

    /**Converts an AttachmentDto to Attachment object and saves both the file
     * on disk and its record in database.
     *
     * @param attachmentFileDto Data transfer object which contains the file
     *                          itself and other data needed to process
     *                          and save the attachment.
     * @throws IOException when there is a problem with either saving the file
     *                     or creating the issue-specific folder.
     * @throws EntityNotFoundException when the requested entity does not exist in the database.
     */
    void saveAttachment(AttachmentFileDto attachmentFileDto) throws IOException;

    /**Deletes both the attachment file and its database record matching an id.
     *
     * @param id Database id of the attachment that is about to be deleted.
     * @throws IOException when there is a problem with deleting the
     *                     attachment file from disk.
     * @throws EntityNotFoundException when the requested entity does not exist in the database.
     */
    void deleteAttachmentById(int id) throws IOException;

    /**Copies the file into the provided http response.
     *
     * @param filename name of the file.
     * @param issueId database id of the issue which the file belongs to.
     * @param response used to send the requested file to user.
     * @throws IOException when there is a problem with downloading the file.
     */
    void downloadAttachment(String filename, String issueId, HttpServletResponse response) throws IOException;
}
