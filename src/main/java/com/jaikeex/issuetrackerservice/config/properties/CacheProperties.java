package com.jaikeex.issuetrackerservice.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "cache.config")
public class CacheProperties {

    private String ehCacheName;
    private String evictionPolicy;
    private int maxEntries;
    private int timeToLiveSeconds;
}
