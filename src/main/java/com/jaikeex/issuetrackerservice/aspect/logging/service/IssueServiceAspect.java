package com.jaikeex.issuetrackerservice.aspect.logging.service;

import com.jaikeex.issuetrackerservice.entity.Issue;
import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class IssueServiceAspect {

    @AfterReturning(value = "com.jaikeex.issuetrackerservice.aspect.pointcut.service.IssueServicePointcuts.deletePointcut(issueService)", argNames = "joinPoint,issueService")
    public void logDeleteIssue(JoinPoint joinPoint, IssueService issueService) {
        Integer id = (Integer) joinPoint.getArgs()[0];
        log.info("Deleted issue from the database [id={}]", id);
    }

    @AfterReturning(value = "com.jaikeex.issuetrackerservice.aspect.pointcut.service.IssueServicePointcuts.savePointcut(issueService)", argNames = "issueService, issue", returning = "issue")
    public void logSaveNewIssue(IssueService issueService, Issue issue) {
        log.info("Saved new issue report to the database [id={}] [title={}]", issue.getId(), issue.getTitle());
    }

    @AfterReturning(value = "com.jaikeex.issuetrackerservice.aspect.pointcut.service.IssueServicePointcuts.updatePropertiesPointcut(issueService)", argNames = "issueService, issue", returning = "issue")
    public void logUpdateProperties(IssueService issueService, Issue issue) {
        log.info("Updated the properties of an issue report in the database [id={}] [title={}]", issue.getId(), issue.getTitle());
    }

    @AfterReturning(value = "com.jaikeex.issuetrackerservice.aspect.pointcut.service.IssueServicePointcuts.updateDescriptionPointcut(issueService)", argNames = "issueService, issue", returning = "issue")
    public void logUpdateDescription(IssueService issueService, Issue issue) {
        log.info("Updated the description of an issue report in the database [id={}] [title={}]", issue.getId(), issue.getTitle());
    }
}
