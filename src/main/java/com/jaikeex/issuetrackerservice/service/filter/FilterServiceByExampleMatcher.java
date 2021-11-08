package com.jaikeex.issuetrackerservice.service.filter;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Primary
@Slf4j
public class FilterServiceByExampleMatcher implements FilterService {

    private final IssueRepository repository;

    @Autowired
    public FilterServiceByExampleMatcher(IssueRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Issue> filterIssues(IssueDto issueDto) {
        IssueType type = issueDto.getType();
        Severity severity = issueDto.getSeverity();
        Project project = issueDto.getProject();
        Status status = issueDto.getStatus();

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Issue> exampleQuery = Example.of(new Issue(type, severity, status, project), matcher);
        List<Issue> results =  repository.findAll(exampleQuery);
        Collections.reverse(results); //in order to display newest at the top.
        return results;
    }
}
