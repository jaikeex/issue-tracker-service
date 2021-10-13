package com.jaikeex.issuetrackerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentBytesDto {
    private String title;
    private byte[] attachmentBytes;
    private String originalFilename;
}
