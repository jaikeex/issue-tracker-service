package com.jaikeex.issuetrackerservice.service;

import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.HistoryRecord;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.HistoryRepository;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HistoryService {

    private final HistoryRepository repository;

    @Autowired
    public HistoryService(HistoryRepository repository) {
        this.repository = repository;
    }

    public void record(RecordType type, Issue issue) {
        HistoryRecord newRecord = new HistoryRecord(type.getTextForDbRecord(issue), issue);
        saveNewRecord(newRecord);
    }

    public void record(RecordType type, Issue issue, Attachment attachment) {
        HistoryRecord newRecord = new HistoryRecord(type.getTextForDbRecord(attachment), issue);
        saveNewRecord(newRecord);
    }

    public void saveNewRecord(HistoryRecord record) {
        repository.save(record);
    }

    public List<HistoryRecord> findRecordsByIssueId(int issueId) {
        return repository.findRecordsByIssueId(issueId);
    }

}
