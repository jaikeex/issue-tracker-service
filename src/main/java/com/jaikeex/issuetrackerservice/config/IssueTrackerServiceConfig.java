package com.jaikeex.issuetrackerservice.config;

import com.jaikeex.issuetrackerservice.controller.AttachmentController;
import com.jaikeex.issuetrackerservice.controller.IssueController;
import com.jaikeex.issuetrackerservice.config.properties.CacheProperties;
import com.jaikeex.issuetrackerservice.config.properties.StorageProperties;
import com.jaikeex.issuetrackerservice.repository.AttachmentRepository;
import com.jaikeex.issuetrackerservice.repository.HistoryRepository;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentService;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentServiceImpl;
import com.jaikeex.issuetrackerservice.service.filter.FilterService;
import com.jaikeex.issuetrackerservice.service.filter.FilterServiceImpl;
import com.jaikeex.issuetrackerservice.service.history.HistoryService;
import com.jaikeex.issuetrackerservice.service.history.HistoryServiceImpl;
import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import com.jaikeex.issuetrackerservice.service.issue.IssueServiceImpl;
import com.jaikeex.issuetrackerservice.service.search.SearchService;
import com.jaikeex.issuetrackerservice.service.search.SearchServiceImpl;
import com.jaikeex.issuetrackerservice.utility.filter.CorsFilter;
import com.jaikeex.issuetrackerservice.utility.html.HtmlParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class initializing all beans of the application.
 */
@Configuration
@EnableConfigurationProperties({CacheProperties.class, StorageProperties.class})
public class IssueTrackerServiceConfig {

    @Bean
    AttachmentService attachmentService(IssueRepository issueRepository, AttachmentRepository attachmentRepository, HistoryService historyService, StorageProperties storageProperties) {
        return new AttachmentServiceImpl(issueRepository, attachmentRepository, historyService, storageProperties);
    }

    @Bean
    IssueService issueService(HistoryService historyService, AttachmentService attachmentService, IssueRepository issueRepository, HtmlParser parser) {
        return new IssueServiceImpl(historyService, attachmentService, issueRepository, parser);
    }

    @Bean
    FilterService filterService(IssueRepository issueRepository) {
        return new FilterServiceImpl(issueRepository);
    }

    @Bean
    SearchService searchService(IssueRepository issueRepository, IssueService issueService) {
        return new SearchServiceImpl(issueRepository, issueService) {
        };
    }

    @Bean
    HistoryService historyService(HistoryRepository historyRepository) {
        return new HistoryServiceImpl(historyRepository);
    }

    @Bean
    IssueController issueController(IssueService issueService, SearchService searchService, FilterService filterService) {
        return new IssueController(issueService, searchService, filterService);
    }

    @Bean
    AttachmentController attachmentController(AttachmentService attachmentService) {
        return new AttachmentController(attachmentService);
    }

    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    HtmlParser htmlParser() {
        return new HtmlParser();
    }



}
