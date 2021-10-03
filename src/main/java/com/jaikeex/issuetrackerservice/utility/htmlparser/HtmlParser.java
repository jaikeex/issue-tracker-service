package com.jaikeex.issuetrackerservice.utility.htmlparser;

import com.jaikeex.issuetrackerservice.dto.DescriptionDto;
import com.jaikeex.issuetrackerservice.entity.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class HtmlParser {
    public void convertNewLinesInDescriptionToHtml(Issue issue) {
        issue.setDescription(replaceNewLinesWithHtml(issue.getDescription()));
        log.debug("Converted new lines in issue description to <br> tags [id={}]", issue.getId());
    }

    public void convertNewLinesInDescriptionToHtml(DescriptionDto descriptionDto) {
        descriptionDto.setDescription(replaceNewLinesWithHtml(descriptionDto.getDescription()));
        log.debug("Converted new lines in descriptionDto description to <br> tags [title={}]", descriptionDto.getTitle());
    }

    private String replaceNewLinesWithHtml(String text) {
        return text.replaceAll("(\r\n|\n)", "<br />");
    }
}
