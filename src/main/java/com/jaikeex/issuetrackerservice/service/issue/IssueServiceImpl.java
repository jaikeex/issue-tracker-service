package com.jaikeex.issuetrackerservice.service.issue;

import com.jaikeex.issuetrackerservice.dto.AttachmentFileDto;
import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.issueProperties.IssueType;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Project;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Severity;
import com.jaikeex.issuetrackerservice.entity.issueProperties.Status;
import com.jaikeex.issuetrackerservice.repository.IssueRepository;
import com.jaikeex.issuetrackerservice.service.attachment.AttachmentService;
import com.jaikeex.issuetrackerservice.service.history.HistoryService;
import com.jaikeex.issuetrackerservice.utility.RecordType;
import com.jaikeex.issuetrackerservice.utility.exception.TitleAlreadyExistsException;
import com.jaikeex.issuetrackerservice.utility.html.HtmlParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class IssueServiceImpl implements IssueService {

    private static final String CACHE_NAME = "issue-cache-eh";
    private static final String ISSUE_NOT_FOUND_EXCEPTION_MESSAGE = "Requested issue report does not exist in the database.";

    private final HistoryService historyService;
    private final AttachmentService attachmentService;
    private final IssueRepository repository;
    private final HtmlParser parser;

    public IssueServiceImpl(HistoryService historyService,
                            AttachmentService attachmentService,
                            IssueRepository repository,
                            HtmlParser parser) {
        this.historyService = historyService;
        this.attachmentService = attachmentService;
        this.repository = repository;
        this.parser = parser;
    }

    /** Converts an IssueDto to Issue object and saves it into the database.
     *
     * @param issueDto Dto with the issue report data.
     * @return saved Issue.
     * @throws IOException when there is a problem with saving any attached files.
     */
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

    /** Deletes the issue report matching an id.
     *
     * @param id id of the issue which is to be deleted.
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteIssueById(Integer id) {
        repository.deleteById(id);
    }

    /** Returns the issue report matching an id.
     *
     * @param id id of the requested issue.
     * @return Issue with the provided id.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'id' + #id")
    public Issue findIssueById(int id) {
        Optional<Issue> issue = repository.findById(id);
        return getIssueFromOptional(issue);
    }

    /** Returns the issue report with a specific title.
     *
     * @param title title of the requested issue.
     * @return Issue with the provided title.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'title' + #title")
    public Issue findIssueByTitle(String title) {
        Optional<Issue> issue = repository.findByTitle(title);
        return getIssueFromOptional(issue);
    }

    /** Returns List of all issues currently present in the database.
     *
     * @return List with all issues found.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<Issue> findAllIssues() {
        return repository.findAllIssues();
    }

    /** Returns all issue reports with a specific type.
     *
     * @param type Requested issue type.
     * @return List of all issues with the provided type.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'type' + #type")
    public List<Issue> findAllIssuesByType(IssueType type) {
        return repository.findAllByType(type);
    }

    /** Returns all issue reports with a specific severity.
     *
     * @param severity Requested issue severity.
     * @return List of all issues with the provided severity.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'severity' + #severity")
    public List<Issue> findAllIssuesBySeverity(Severity severity) {
        return repository.findAllBySeverity(severity);
    }

    /** Returns all issue reports with a specific status.
     *
     * @param status Requested issue status.
     * @return List of all issues with the provided status.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'status' + #status")
    public List<Issue> findAllIssuesByStatus(Status status) {
        return repository.findAllByStatus(status);
    }

    /** Returns all issue reports with a specific project.
     *
     * @param project Requested issue project.
     * @return List of all issues with the provided project.
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'project' + #project")
    public List<Issue> findAllIssuesByProject(Project project) {
        return repository.findAllByProject(project);
    }

    /** Updates an issue in database with new properties. The specific report
     * which is to be updated is specified by an id contained in the IssueDto
     * provided.
     *
     * @param issueDto Dto with all the necessary data.
     * @return updated issue.
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Issue updateIssueWithNewProperties(IssueDto issueDto) {
        Optional<Issue> updatedIssue = changePropertiesInDatabaseById(issueDto);
        return getIssueFromOptional(updatedIssue);
    }

    /** Updates an issue in database with new properties. The specific report
     * which is to be updated is specified by an id contained in the IssueDto
     * provided.
     *
     * @param issueDto Dto with all the necessary data.
     * @return updated issue.
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public Issue updateIssueWithNewDescription(IssueDto issueDto) {
        Optional<Issue> updatedIssue = updateIssueDescriptionInDatabase(issueDto);
        return getIssueFromOptional(updatedIssue);
    }


    /** Checks whether an issue report already exists in the database.
     * @return true if issue can be created, false otherwise.
     * @throws TitleAlreadyExistsException when the issue already exists.
     */
    private boolean canBeCreated(Issue issue) {
        Issue dbResponse = repository.findIssueByTitle(issue.getTitle());
        if (dbResponse != null) {
            throw new TitleAlreadyExistsException();
        } else {
            return true;
        }
    }

    /** Returns the issue inside the Optional container.
     * @return the retrieved issue
     * @throws EntityNotFoundException if there is nothing in the Optional.
     */
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
        return repository.findById(id);

    }

    private Optional<Issue> updateIssueDescriptionInDatabase(IssueDto issueDto) {
        parser.convertNewLinesInDescriptionToHtml(issueDto);
        repository.updateIssueWithNewDescription(
                issueDto.getTitle(), issueDto.getDescription());
        Optional<Issue> updatedIssue = repository.findByTitle(issueDto.getTitle());
        //TODO: the Optional is handled twice: here and in the calling method, this should not be the case.
        historyService.record(RecordType.UPDATE_DESCRIPTION, getIssueFromOptional(updatedIssue));
        return updatedIssue;
    }
}
