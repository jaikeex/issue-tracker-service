package com.jaikeex.issuetrackerservice.service.history;

import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.HistoryRecord;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.repository.HistoryRepository;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository repository;

    @Autowired
    public HistoryServiceImpl(HistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(RecordType type, Issue issue) {
        HistoryRecord newRecord = new HistoryRecord(type.getTextForDbRecord(issue), issue);
        saveNewRecord(newRecord);
    }

    @Override
    public void record(RecordType type, Issue issue, Attachment attachment) {
        HistoryRecord newRecord = new HistoryRecord(type.getTextForDbRecord(attachment), issue);
        saveNewRecord(newRecord);
    }

    protected void saveNewRecord(HistoryRecord record) {
        repository.save(record);
    }

    public List<HistoryRecord> findRecordsByIssueId(int issueId) {
        return repository.findRecordsByIssueId(issueId);
    }
}
