package com.jaikeex.issuetrackerservice.service.issue;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Manages all issue related operations.
 */
public interface IssueService {

    /** Converts an IssueDto to Issue object and saves it into the database.
     *
     * @param issueDto Dto with the issue report data.
     * @return saved Issue.
     * @throws IOException when there is a problem with saving any attached files.
     */
    Issue saveNewIssue(IssueDto issueDto) throws IOException;

    /** Deletes the issue report matching an id.
     *
     * @param id id of the issue which is to be deleted.
     */
    void deleteIssueById(Integer id);

    /** Returns the issue report matching an id.
     *
     * @param id id of the requested issue.
     * @return Issue with the provided id.
     */
    @Transactional(readOnly = true)
    Issue findIssueById(int id);

    /** Returns the issue report with a specific title.
     *
     * @param title title of the requested issue.
     * @return Issue with the provided title.
     */
    @Transactional(readOnly = true)
    Issue findIssueByTitle(String title);

    /** Returns List of all issues currently present in the database.
     *
     * @return List with all issues found.
     */
    @Transactional(readOnly = true)
    List<Issue> findAllIssues();

    /** Returns all issue reports with a specific type.
     *
     * @param type Requested issue type.
     * @return List of all issues with the provided type.
     */
    @Transactional(readOnly = true)
    List<Issue> findAllIssuesByType(IssueType type);

    /** Returns all issue reports with a specific severity.
     *
     * @param severity Requested issue severity.
     * @return List of all issues with the provided severity.
     */
    @Transactional(readOnly = true)
    List<Issue> findAllIssuesBySeverity(Severity severity);

    /** Returns all issue reports with a specific status.
     *
     * @param status Requested issue status.
     * @return List of all issues with the provided status.
     */
    @Transactional(readOnly = true)
    List<Issue> findAllIssuesByStatus(Status status);

    /** Returns all issue reports with a specific project.
     *
     * @param project Requested issue project.
     * @return List of all issues with the provided project.
     */
    @Transactional(readOnly = true)
    List<Issue> findAllIssuesByProject(Project project);

    /** Updates an issue in database with new properties. The specific report
     * which is to be updated is specified by an id contained in the IssueDto
     * provided.
     *
     * @param issueDto Dto with all the necessary data.
     * @return updated issue.
     */
    Issue updateIssueWithNewProperties(IssueDto issueDto);

    /** Updates an issue in database with new properties. The specific report
     * which is to be updated is specified by an id contained in the IssueDto
     * provided.
     *
     * @param issueDto Dto with all the necessary data.
     * @return updated issue.
     */
    Issue updateIssueWithNewDescription(IssueDto issueDto);

}
