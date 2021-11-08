package com.jaikeex.issuetrackerservice.service.history;

import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.utility.RecordType;

public interface HistoryService {

    /**Creates an entry in database with information about a change to the issue report.
     * @param type Type of the change.
     * @param issue Changed issue.
     */
    void record(RecordType type, Issue issue);

    /**Creates an entry in database with information about a change to the issue report.
     * @param type Type of the change.
     * @param issue Changed issue.
     * @param attachment Changed (uploaded/deleted) attachment.
     */
    void record(RecordType type, Issue issue, Attachment attachment);
}
