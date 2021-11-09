package com.jaikeex.issuetrackerservice;

import com.jaikeex.issuetrackerservice.config.IssueTrackerServiceConfig;
import com.jaikeex.issuetrackerservice.config.cache.CacheConfig;
import com.jaikeex.issuetrackerservice.config.swagger.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({IssueTrackerServiceConfig.class, CacheConfig.class, SwaggerConfig.class})
public class IssueTrackerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssueTrackerServiceApplication.class, args);
	}
}
