package com.jaikeex.issuetrackerservice.service.filter;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;

import java.util.List;

public interface FilterService {

    /** Returns those issue reports from database that match a given set of properties.
     * @param issueDto Dto with values of those properties that should be matched by the filter.
     * @return List of issues matching the filter.
     */
    List<Issue> filterIssues(IssueDto issueDto);
}
