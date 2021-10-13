package com.jaikeex.issuetrackerservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public Attachment(String path, Issue issue) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.setPath(path);
        this.setIssue(issue);
        this.setDate(now);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String path;
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "issueid")
    @JsonIgnore
    private Issue issue;

}
