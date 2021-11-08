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
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    Optional<Issue> findByTitle(String title);

    List<Issue> findAllByStatus(Status status);

    List<Issue> findAllByType(IssueType type);

    List<Issue> findAllBySeverity(Severity severity);

    List<Issue> findAllByProject(Project project);

    @Query("SELECT i FROM Issue i ORDER BY i.id DESC")
    List<Issue> findAllIssues();

    @Query("SELECT i FROM Issue i WHERE i.title = :title")
    Issue findIssueByTitle(
            @Param("title") String title);

    @Query("SELECT i FROM Issue i WHERE i.id = :id")
    Issue findIssueById(
            @Param("id") Integer id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Issue SET type = :type WHERE id = :id")
    void updateIssueWithNewType(
            @Param("id") Integer id,
            @Param("type") IssueType type);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Issue SET severity = :severity WHERE id = :id")
    void updateIssueWithNewSeverity(
            @Param("id") Integer id,
            @Param("severity") Severity severity);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Issue SET status = :status WHERE id = :id")
    void updateIssueWithNewStatus(
            @Param("id") Integer id,
            @Param("status") Status status);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Issue SET project = :project WHERE id = :id")
    void updateIssueWithNewProject(
            @Param("id") Integer id,
            @Param("project") Project project);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Issue SET description = :description WHERE title = :title")
    void updateIssueWithNewDescription(
            @Param("title") String title,
            @Param("description") String description);

}
