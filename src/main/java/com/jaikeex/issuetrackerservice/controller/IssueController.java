package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.service.filter.FilterService;
import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import com.jaikeex.issuetrackerservice.service.search.SearchService;
import com.jaikeex.issuetrackerservice.utility.exception.TitleAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RestController
@RequestMapping("/issue")
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final SearchService searchService;
    private final FilterService filterService;

    @Autowired
    public IssueController(IssueService issueService,
                           SearchService searchService,
                           FilterService filterService) {
        this.issueService = issueService;
        this.searchService = searchService;
        this.filterService = filterService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> findIssueById(@PathVariable Integer id) {
        Issue issue = issueService.findIssueById(id);
        return getFindIssueResponseEntity(issue);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Object> findIssueByTitle(@PathVariable String title) {
        Issue issue = issueService.findIssueByTitle(title);
        return getFindIssueResponseEntity(issue);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllIssues() {
        List<Issue> issues = issueService.findAllIssues();
        return getFindIssueResponseEntity(issues);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Object> findAllByType(@PathVariable IssueType type) {
        List<Issue> issues = issueService.findAllIssuesByType(type);
        return getFindIssueResponseEntity(issues);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<Object> findAllBySeverity(@PathVariable Severity severity) {
        List<Issue> issues = issueService.findAllIssuesBySeverity(severity);
        return getFindIssueResponseEntity(issues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> findAllByStatus(@PathVariable Status status) {
        List<Issue> issues = issueService.findAllIssuesByStatus(status);
        return getFindIssueResponseEntity(issues);
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<Object> findAllByProject(@PathVariable Project project) {
        List<Issue> issues = issueService.findAllIssuesByProject(project);
        return getFindIssueResponseEntity(issues);
    }

    @PostMapping("/filter")
    public ResponseEntity<Object> filterIssues(@RequestBody IssueDto issueDto) {
        List<Issue> issues = filterService.filterIssues(issueDto);
        return getFindIssueResponseEntity(issues);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateIssueWithNewProperties(@RequestBody IssueDto issueDto) {
        Issue updatedIssue = issueService.updateIssueWithNewProperties(issueDto);
        return ResponseEntity.ok().body(updatedIssue);
    }

    @PostMapping("/update-description")  // Is a POST endpoint because html forms can't handle PUT method.
    public ResponseEntity<Object> updateIssueWithNewDescription(
            @RequestBody IssueDto issueDto) {
        Issue updatedIssue = issueService.updateIssueWithNewDescription(issueDto);
        return ResponseEntity.ok().body(updatedIssue);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchIssuesGet(@RequestParam(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        return getFindIssueResponseEntity(issues);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNewIssue(@RequestBody IssueDto issueDto)
            throws IOException {
        Issue issue = issueService.saveNewIssue(issueDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteIssueById(@PathVariable Integer id) {
        issueService.deleteIssueById(id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<Object> getFindIssueResponseEntity(Issue issue) {
        HttpHeaders headers = getJsonHttpHeaders();
        return ResponseEntity.ok().headers(headers).body(issue);
    }

    private ResponseEntity<Object> getFindIssueResponseEntity(List<Issue> issues) {
        HttpHeaders headers = getJsonHttpHeaders();
        return ResponseEntity.ok().headers(headers).body(issues);
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




