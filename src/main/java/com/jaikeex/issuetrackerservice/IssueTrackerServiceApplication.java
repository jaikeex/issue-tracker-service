package com.jaikeex.issuetrackerservice;

import com.jaikeex.issuetrackerservice.config.cache.CacheConfigProperties;
import com.jaikeex.issuetrackerservice.config.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableConfigurationProperties({CacheConfigProperties.class, StorageProperties.class})
public class IssueTrackerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueTrackerServiceApplication.class, args);
	}
}
