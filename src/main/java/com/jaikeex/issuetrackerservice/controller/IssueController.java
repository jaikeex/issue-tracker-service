package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
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

import java.util.List;

@RestController
@RequestMapping("/issue")
@Slf4j
public class IssueController {

    IssueService service;
    SearchService searchService;
    FilterService filterService;

    @Autowired
    public IssueController(IssueService service,
                           SearchService searchService,
                           FilterService filterService) {
        this.service = service;
        this.searchService = searchService;
        this.filterService = filterService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> findIssueById(@PathVariable Integer id) {
        Issue issue = service.findIssueById(id);
        return getFindIssueResponseEntity(issue);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Object> findIssueByTitle(@PathVariable String title) {
        Issue issue = service.findIssueByTitle(title);
        return getFindIssueResponseEntity(issue);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllIssues() {
        List<Issue> issues = service.findAllIssues();
        return getFindListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Object> findAllByType(@PathVariable IssueType type) {
        List<Issue> issues = service.findAllIssuesByType(type);
        return getFindListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<Object> findAllBySeverity(@PathVariable Severity severity) {
        List<Issue> issues = service.findAllIssuesBySeverity(severity);
        return getFindListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> findAllByStatus(@PathVariable Status status) {
        List<Issue> issues = service.findAllIssuesByStatus(status);
        return getFindListOfIssuesResponseEntity(issues);
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<Object> findAllByProject(@PathVariable Project project) {
        List<Issue> issues = service.findAllIssuesByProject(project);
        return getFindListOfIssuesResponseEntity(issues);
    }

    @PostMapping("/filter")
    public ResponseEntity<Object> filterIssues(@RequestBody FilterDto filterDto) {
        List<Issue> issues = filterService.filterIssues(filterDto);
        return getFilterResponseEntity(issues);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateIssueWithNewProperties(@RequestBody Issue issue) {
        Issue updatedIssue = service.updateIssueWithNewProperties(issue);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }

    @PostMapping("/update-description")  // Is a POST endpoint because html forms can't handle PUT method.
    public ResponseEntity<Object> updateIssueWithNewDescription(
            @RequestBody DescriptionDto descriptionDto) {
        System.out.println(descriptionDto);
        Issue updatedIssue = service.updateIssueWithNewDescription(descriptionDto);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchIssuesGet(
            @RequestParam(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        return getSearchResponseEntity(issues);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNewIssue(@RequestBody Issue issue) {
        try {
            service.saveIssueToDatabase(issue);
        } catch (TitleAlreadyExistsException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(issue, HttpStatus.CREATED);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteIssueById(@PathVariable Integer id) {
        service.deleteIssueById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Deprecated
    @PostMapping("/search")
    public ResponseEntity<Object> searchIssuesPost(
            @RequestBody(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        return getSearchResponseEntity(issues);
    }

    private ResponseEntity<Object> getFindIssueResponseEntity(Issue issue) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issue, headers, getFindIssueHttpStatus(issue));
    }

    private ResponseEntity<Object> getFindListOfIssuesResponseEntity(List<Issue> issues) {
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    private ResponseEntity<Object> getFilterResponseEntity(List<Issue> issues) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issues, headers, HttpStatus.OK);
    }

    private ResponseEntity<Object> getSearchResponseEntity(List<Issue> issues) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issues, headers, HttpStatus.OK);
    }

    private HttpStatus getFindIssueHttpStatus(Issue issue) {
        if (issue != null) {
            return  HttpStatus.OK;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }


}




