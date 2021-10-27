package com.jaikeex.issuetrackerservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRecord {

    public HistoryRecord(String text, Issue issue) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.setText(text);
        this.setIssue(issue);
        this.setDate(now);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "issueid")
    @JsonIgnore
    private Issue issue;

}
