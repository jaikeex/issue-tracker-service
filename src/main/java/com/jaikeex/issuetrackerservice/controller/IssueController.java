package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.dto.AttachmentBytesDto;
import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.service.AttachmentService;
import com.jaikeex.issuetrackerservice.service.FilterService;
import com.jaikeex.issuetrackerservice.service.IssueService;
import com.jaikeex.issuetrackerservice.service.SearchService;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/issue")
@Slf4j
public class IssueController {

    private final IssueService issueService;
    private final SearchService searchService;
    private final FilterService filterService;
    private final AttachmentService attachmentService;

    @Autowired
    public IssueController(IssueService issueService,
                           SearchService searchService,
                           FilterService filterService,
                           AttachmentService attachmentService) {
        this.issueService = issueService;
        this.searchService = searchService;
        this.filterService = filterService;
        this.attachmentService = attachmentService;
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
        return getListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Object> findAllByType(@PathVariable IssueType type) {
        List<Issue> issues = issueService.findAllIssuesByType(type);
        return getListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<Object> findAllBySeverity(@PathVariable Severity severity) {
        List<Issue> issues = issueService.findAllIssuesBySeverity(severity);
        return getListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> findAllByStatus(@PathVariable Status status) {
        List<Issue> issues = issueService.findAllIssuesByStatus(status);
        return getListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<Object> findAllByProject(@PathVariable Project project) {
        List<Issue> issues = issueService.findAllIssuesByProject(project);
        return getListOfIssuesResponseEntity(issues);
    }

    @PostMapping("/filter")
    public ResponseEntity<Object> filterIssues(@RequestBody FilterDto filterDto) {
        List<Issue> issues = filterService.filterIssues(filterDto);
        return getListOfIssuesResponseEntity(issues);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateIssueWithNewProperties(@RequestBody Issue issue) {
        Issue updatedIssue = issueService.updateIssueWithNewProperties(issue);
        return ResponseEntity.ok().body(updatedIssue);
    }

    @PostMapping("/update-description")  // Is a POST endpoint because html forms can't handle PUT method.
    public ResponseEntity<Object> updateIssueWithNewDescription(
            @RequestBody DescriptionDto descriptionDto) {
        Issue updatedIssue = issueService.updateIssueWithNewDescription(descriptionDto);
        return ResponseEntity.ok().body(updatedIssue);
    }

    @PostMapping("/upload-attachment")
    public ResponseEntity<Object> uploadNewAttachment(
            @RequestBody AttachmentBytesDto attachmentBytesDto) throws IOException {
        Issue updatedIssue = attachmentService.save(attachmentBytesDto);
        return ResponseEntity.ok().body(updatedIssue);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchIssuesGet(
            @RequestParam(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        return getListOfIssuesResponseEntity(issues);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNewIssue(@RequestBody Issue issue) {
        issueService.saveIssueToDatabase(issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(issue);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteIssueById(@PathVariable Integer id) {
        issueService.deleteIssueById(id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<Object> getFindIssueResponseEntity(Issue issue) {
        HttpHeaders headers = getJsonHttpHeaders();
        return ResponseEntity.status(getFindIssueHttpStatus(issue)).headers(headers).body(issue);
    }

    private ResponseEntity<Object> getListOfIssuesResponseEntity(List<Issue> issues) {
        HttpHeaders headers = getJsonHttpHeaders();
        return ResponseEntity.ok().headers(headers).body(issues);
    }

    private HttpStatus getFindIssueHttpStatus(Issue issue) {
        if (issue != null) {
            return  HttpStatus.OK;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }

    private HttpHeaders getJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @ExceptionHandler(TitleAlreadyExistsException.class)
    public ResponseEntity<Object> TitleAlreadyExists(TitleAlreadyExistsException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> attachmentServiceException(IOException exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}




