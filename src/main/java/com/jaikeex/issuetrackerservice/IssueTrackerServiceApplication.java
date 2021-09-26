package com.jaikeex.issuetrackerservice;

import com.jaikeex.issuetrackerservice.config.CacheConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(CacheConfigProperties.class)
public class IssueTrackerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueTrackerServiceApplication.class, args);
	}
}
