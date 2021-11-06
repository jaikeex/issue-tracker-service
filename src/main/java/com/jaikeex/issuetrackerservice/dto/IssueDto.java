package com.jaikeex.issuetrackerservice.dto;

import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {

    private Integer id;

    private String title;
    private String description;
    private String author;

    private IssueType type;
    private Severity severity;
    private Project project;
    private Status status;

    private AttachmentFileDto attachmentFileDto;
}
