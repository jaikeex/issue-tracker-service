package com.jaikeex.issuetrackerservice.controller;

import com.jaikeex.issuetrackerservice.Dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.Dto.FilterDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.service.IssueService;
import com.jaikeex.issuetrackerservice.service.SearchService;
import com.jaikeex.issuetrackerservice.utility.exceptions.TitleAlreadyExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issue")
@Log4j2
public class IssueController {

    IssueService service;
    SearchService searchService;

    @Autowired
    public IssueController(IssueService service, SearchService searchService) {
        this.service = service;
        this.searchService = searchService;
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
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Object> findAllByType(@PathVariable IssueType type) {
        List<Issue> issues = service.findAllIssuesByType(type);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<Object> findAllBySeverity(@PathVariable Severity severity) {
        List<Issue> issues = service.findAllIssuesBySeverity(severity);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> findAllByStatus(@PathVariable Status status) {
        List<Issue> issues = service.findAllIssuesByStatus(status);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @GetMapping("/project/{project}")
    public ResponseEntity<Object> findAllByProject(@PathVariable Project project) {
        List<Issue> issues = service.findAllIssuesByProject(project);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public ResponseEntity<Object> filterIssues(@RequestBody FilterDto filterDto) {
        List<Issue> issues = service.filterIssues(filterDto);
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issues, headers, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateIssueWithNewProperties(@RequestBody Issue issue) {
        Issue updatedIssue = service.updateIssueWithNewProperties(issue);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }

    @PostMapping("/update-description")  // Is a POST endpoint because html forms can't handle PUT method.
    public ResponseEntity<Object> updateIssueWithNewDescription(@RequestBody DescriptionDto descriptionDto) {
        System.out.println(descriptionDto);
        Issue updatedIssue = service.updateIssueWithNewDescription(descriptionDto);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchIssuesGet(@RequestParam(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issues, headers, HttpStatus.OK);
    }

    @Deprecated
    @PostMapping("/search")
    public ResponseEntity<Object> searchIssuesPost(@RequestBody(required = false) String query) {
        List<Issue> issues = searchService.searchIssues(query);
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issues, headers, HttpStatus.OK);
    }

    private ResponseEntity<Object> getFindIssueResponseEntity(Issue issue) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(issue, headers, getFindIssueHttpStatus(issue));
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




