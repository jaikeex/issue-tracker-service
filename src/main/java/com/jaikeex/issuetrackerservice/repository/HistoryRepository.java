package com.jaikeex.issuetrackerservice.repository;

import com.jaikeex.issuetrackerservice.entity.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryRecord, Integer> {

    List<HistoryRecord> findRecordsByIssueId(int issueId);
}
