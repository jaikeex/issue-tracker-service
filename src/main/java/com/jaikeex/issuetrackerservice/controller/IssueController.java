package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.issueProperties.IssueType;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Project;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Severity;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Status;
import com.jaikeex.issuetrackerservice.service.filter.FilterService;
import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import com.jaikeex.issuetrackerservice.service.search.SearchService;
import com.jaikeex.issuetrackerservice.utility.exception.TitleAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * API for all issue-specific operations.
 */
@RequestMapping("/issue")
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final SearchService searchService;
    private final FilterService filterService;

    public IssueController(IssueService issueService,
                           SearchService searchService,
                           FilterService filterService) {
        this.issueService = issueService;
        this.searchService = searchService;
        this.filterService = filterService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Issue> findIssueById(@PathVariable Integer id) {
        Issue issue = issueService.findIssueById(id);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issue);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Issue> findIssueByTitle(@PathVariable String title) {
        Issue issue = issueService.findIssueByTitle(title);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issue);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Issue>> findAllIssues() {
        List<Issue> issues = issueService.findAllIssues();
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Issue>> findAllByType(@PathVariable IssueType type) {
        List<Issue> issues = issueService.findAllIssuesByType(type);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Issue>> findAllBySeverity(@PathVariable Severity severity) {
        List<Issue> issues = issueService.findAllIssuesBySeverity(severity);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Issue>> findAllByStatus(@PathVariable Status status) {
        List<Issue> issues = issueService.findAllIssuesByStatus(status);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<List<Issue>> findAllByProject(@PathVariable Project project) {
        List<Issue> issues = issueService.findAllIssuesByProject(project);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Issue>> filterIssues(@RequestBody IssueDto issueDto) {
        List<Issue> issues = filterService.filterIssues(issueDto);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @PutMapping("/update")
    public ResponseEntity<Issue> updateIssueWithNewProperties(@RequestBody IssueDto issueDto) {
        Issue updatedIssue = issueService.updateIssueWithNewProperties(issueDto);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(updatedIssue);
    }

    @PostMapping("/update-description")
    public ResponseEntity<Issue> updateIssueWithNewDescription(
            @RequestBody IssueDto issueDto) {
        Issue updatedIssue = issueService.updateIssueWithNewDescription(issueDto);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(updatedIssue);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Issue>> searchIssuesGet(@RequestParam(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        return ResponseEntity.ok().headers(getJsonHttpHeaders()).body(issues);
    }

    @PostMapping("/create")
    public ResponseEntity<Issue> createNewIssue(@RequestBody IssueDto issueDto)
            throws IOException {
        Issue issue = issueService.saveNewIssue(issueDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteIssueById(@PathVariable Integer id) {
        issueService.deleteIssueById(id);
        return ResponseEntity.ok().build();
    }

    private HttpHeaders getJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @ExceptionHandler(TitleAlreadyExistsException.class)
    public ResponseEntity<Object> handleTitleAlreadyExistsException(TitleAlreadyExistsException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}




