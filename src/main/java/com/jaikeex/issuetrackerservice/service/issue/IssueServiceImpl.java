package com.jaikeex.issuetrackerservice.service.issue;

import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentService;
import com.jaikeex.issuetrackerservice.service.history.HistoryService;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import com.jaikeex.issuetrackerservice.utility.exception.TitleAlreadyExistsException;
import com.jaikeex.issuetrackerservice.utility.html.HtmlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IssueServiceImpl implements IssueService {

    private static final String CACHE_NAME = "issue-cache-eh";
    private static final String ISSUE_NOT_FOUND_EXCEPTION_MESSAGE = "Requested issue report does not exist in the database.";

    private final HistoryService historyService;
    private final AttachmentService attachmentService;
    private final IssueRepository repository;
    private final HtmlParser parser;

    @Autowired
    public IssueServiceImpl(HistoryService historyService,
                            AttachmentService attachmentService,
                            IssueRepository repository,
                            HtmlParser parser) {
        this.historyService = historyService;
        this.attachmentService = attachmentService;
        this.repository = repository;
        this.parser = parser;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Issue saveNewIssue(IssueDto issueDto) throws IOException {
        Issue issue = new Issue(issueDto);
        if(canBeCreated(issue)) {
            saveIssueToDatabase(issue);
            saveAttachedFilesToDatabase(issueDto);
        }
        return issue;
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteIssueById(Integer id) {
        repository.deleteById(id);
        log.info("Deleted issue from the database [id={}]", id);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'id' + #id")
    public Issue findIssueById(int id) {
        Optional<Issue> issue = repository.findById(id);
        return getIssueFromOptional(issue);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'title' + #title")
    public Issue findIssueByTitle(String title) {
        Optional<Issue> issue = repository.findByTitle(title);
        return getIssueFromOptional(issue);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<Issue> findAllIssues() {
        return repository.findAllIssues();
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'type' + #type")
    public List<Issue> findAllIssuesByType(IssueType type) {
        return repository.findAllByType(type);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'severity' + #severity")
    public List<Issue> findAllIssuesBySeverity(Severity severity) {
        return repository.findAllBySeverity(severity);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'status' + #status")
    public List<Issue> findAllIssuesByStatus(Status status) {
        return repository.findAllByStatus(status);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'project' + #project")
    public List<Issue> findAllIssuesByProject(Project project) {
        return repository.findAllByProject(project);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Issue updateIssueWithNewProperties(IssueDto issueDto) {
        Optional<Issue> updatedIssue = changePropertiesInDatabaseById(issueDto);
        return getIssueFromOptional(updatedIssue);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Issue updateIssueWithNewDescription(IssueDto issueDto) {
        Optional<Issue> updatedIssue = updateIssueDescriptionInDatabase(issueDto);
        return getIssueFromOptional(updatedIssue);
    }

    private boolean canBeCreated(Issue issue) {
        Issue dbResponse = repository.findIssueByTitle(issue.getTitle());
        if (dbResponse != null) {
            throw new TitleAlreadyExistsException();
        } else {
            return true;
        }
    }

    private Issue getIssueFromOptional(Optional<Issue> issue) {
        if (issue.isPresent()) {
            return issue.get();
        }
        throw new EntityNotFoundException(ISSUE_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    private void saveIssueToDatabase(Issue issue) {
        parser.convertNewLinesInDescriptionToHtml(issue);
        repository.save(issue);
        historyService.record(RecordType.CREATE, issue);
        log.info("Saved new issue report to the database [id={}] [title={}]", issue.getId(), issue.getTitle());
    }

    private void saveAttachedFilesToDatabase(IssueDto issueDto) throws IOException {
        AttachmentFileDto attachmentFileDto = issueDto.getAttachmentFileDto();
        if (attachmentFileDto.getBytes().length != 0) {
            attachmentService.saveAttachment(attachmentFileDto);
        }
    }

    private Optional<Issue> changePropertiesInDatabaseById(IssueDto issueDto) {
        int id = issueDto.getId();
        Issue issue = getIssueFromOptional(repository.findById(id));
        repository.updateIssueWithNewType(id, issueDto.getType());
        repository.updateIssueWithNewSeverity(id, issueDto.getSeverity());
        repository.updateIssueWithNewStatus(id, issueDto.getStatus());
        repository.updateIssueWithNewProject(id, issueDto.getProject());
        historyService.record(RecordType.UPDATE_PROPERTIES, issue);
        log.info("Updated the properties of an issue report in the database [id={}]", issue.getId());
        return repository.findById(id);

    }

    private Optional<Issue> updateIssueDescriptionInDatabase(IssueDto issueDto) {
        parser.convertNewLinesInDescriptionToHtml(issueDto);
        repository.updateIssueWithNewDescription(
                issueDto.getTitle(), issueDto.getDescription());
        log.info("Updated the description of an issue report in the database [title={}]", issueDto.getTitle());
        Optional<Issue> updatedIssue = repository.findByTitle(issueDto.getTitle());
        //TODO: the Optional is handled twice: here and in the calling method, this should not be the case.
        historyService.record(RecordType.UPDATE_DESCRIPTION, getIssueFromOptional(updatedIssue));
        return updatedIssue;
    }
}
