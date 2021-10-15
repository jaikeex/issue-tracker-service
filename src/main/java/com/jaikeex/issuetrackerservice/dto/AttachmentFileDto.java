package com.jaikeex.issuetrackerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentFileDto {
    private String issueTitle;
    private byte[] bytes;
    private String originalFilename;
}
