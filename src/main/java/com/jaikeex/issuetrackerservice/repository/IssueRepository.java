package com.jaikeex.issuetrackerservice.repository;

import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.entity.properties.IssueType;
import com.jaikeex.issuetrackerservice.entity.properties.Project;
import com.jaikeex.issuetrackerservice.entity.properties.Severity;
import com.jaikeex.issuetrackerservice.entity.properties.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    @Query("SELECT i FROM Issue i ORDER BY i.id DESC ")
    List<Issue> findAllIssues();


    @Query("select i from Issue i where i.title = :title")
    Issue findIssueByTitle(
            @Param("title") String title);

    @Query("select i from Issue i where i.id = :id")
    Issue findIssueById(
            @Param("id") Integer id);

    @Query("select i from Issue i where i.type = :type")
    List<Issue> findAllIssuesByType(
            @Param("type") IssueType type);

    @Query("select i from Issue i where i.severity = :severity")
    List<Issue> findAllIssuesBySeverity(
            @Param("severity") Severity severity);

    @Query("select i from Issue i where i.status = :status")
    List<Issue> findAllIssuesByStatus(
            @Param("status") Status status);

    @Query("select i from Issue i where i.project = :project")
    List<Issue> findAllIssuesByProject(
            @Param("project") Project project);

    @Modifying
    @Transactional
    @Query("update Issue set type = :type where id = :id")
    void updateIssueWithNewType(
            @Param("id") Integer id,
            @Param("type") IssueType type);

    @Modifying
    @Transactional
    @Query("update Issue set severity = :severity where id = :id")
    void updateIssueWithNewSeverity(
            @Param("id") Integer id,
            @Param("severity") Severity severity);

    @Modifying
    @Transactional
    @Query("update Issue set status = :status where id = :id")
    void updateIssueWithNewStatus(
            @Param("id") Integer id,
            @Param("status") Status status);

    @Modifying
    @Transactional
    @Query("update Issue set project = :project where id = :id")
    void updateIssueWithNewProject(
            @Param("id") Integer id,
            @Param("project") Project project);

    @Modifying
    @Transactional
    @Query("update Issue set description = :description where title = :title")
    void updateIssueWithNewDescription(
            @Param("title") String title,
            @Param("description") String description);

}
