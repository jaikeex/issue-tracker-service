package com.jaikeex.issuetrackerservice.config.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "storage.folder")
public class StorageProperties {

    private String issueAttachmentsFolder;
    private String attachmentDownloadEndpoint;
    
}
