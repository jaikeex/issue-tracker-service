package com.jaikeex.issuetrackerservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    public Attachment(String path, AttachmentFileDto attachmentFileDto, Issue issue) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.setPath(path);
        this.setOriginalFilename(attachmentFileDto.getOriginalFilename());
        this.setIssue(issue);
        this.setDate(now);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String path;
    private String originalFilename;
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "issueid")
    @JsonIgnore
    private Issue issue;

}
