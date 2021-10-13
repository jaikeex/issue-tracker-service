package com.jaikeex.issuetrackerservice.repository;

import com.jaikeex.issuetrackerservice.entity.Attachment;
import com.jaikeex.issuetrackerservice.entity.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
