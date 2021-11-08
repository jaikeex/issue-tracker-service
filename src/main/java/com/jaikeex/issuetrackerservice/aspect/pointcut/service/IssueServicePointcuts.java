package com.jaikeex.issuetrackerservice.aspect.pointcut.service;

import com.jaikeex.issuetrackerservice.service.issue.IssueService;
import org.aspectj.lang.annotation.Pointcut;

public class IssueServicePointcuts {

    @Pointcut("execution(* deleteIssueById(..)) && target(issueService)")
    public void deletePointcut(IssueService issueService) {
    }

    @Pointcut("execution(* saveNewIssue(..)) && target(issueService)")
    public void savePointcut(IssueService issueService) {
    }

    @Pointcut("execution(* updateIssueWithNewProperties(..)) && target(issueService)")
    public void updatePropertiesPointcut(IssueService issueService) {
    }

    @Pointcut("execution(* updateIssueWithNewDescription(..)) && target(issueService)")
    public void updateDescriptionPointcut(IssueService issueService) {
    }
}