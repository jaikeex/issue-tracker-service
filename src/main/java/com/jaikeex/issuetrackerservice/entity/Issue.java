package com.jaikeex.issuetrackerservice.entity;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"historyRecords", "attachments"})
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
    private List<HistoryRecord> historyRecords;

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
    private List<Attachment> attachments;

    private Timestamp date;
    private String author;

    private IssueType type;
    private Severity severity;
    private Status status;
    private Project project;

    public Issue(IssueDto issueDto) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        this.setDate(now);
        this.setStatus(Status.SUBMITTED);
        this.setAuthor(issueDto.getAuthor());
        this.setTitle(issueDto.getTitle());
        this.setDescription(issueDto.getDescription());
        this.setType(issueDto.getType());
        this.setSeverity(issueDto.getSeverity());
        this.setProject(issueDto.getProject());
    }

    public String propertiesToString() {
        return type.toString() + " " + severity.toString() + " " + project.toString() + "; status: " + status.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return getId() == issue.getId() && getTitle().equals(issue.getTitle()) && getAuthor().equals(issue.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAuthor());
    }
}


