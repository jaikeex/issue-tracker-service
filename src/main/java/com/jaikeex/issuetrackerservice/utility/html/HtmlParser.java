package com.jaikeex.issuetrackerservice.utility.html;

import com.jaikeex.issuetrackerservice.dto.IssueDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class HtmlParser {
    public void convertNewLinesInDescriptionToHtml(Issue issue) {
        String convertedText = replaceNewLinesWithHtml(issue.getDescription());
        issue.setDescription(convertedText);
        log.debug("Converted new lines in issue description to <br> tags [title={}]", issue.getTitle());
    }

    public void convertNewLinesInDescriptionToHtml(IssueDto issueDto) {
        String convertedText = replaceNewLinesWithHtml(issueDto.getDescription());
        issueDto.setDescription(convertedText);
        log.debug("Converted new lines in descriptionDto description to <br> tags [title={}]", issueDto.getTitle());
    }

    private String replaceNewLinesWithHtml(String text) {
        return text.replaceAll("(\r\n|\n)", "<br />");
    }
}
