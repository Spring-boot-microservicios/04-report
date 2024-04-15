package com.angelfg.reportms.helpers;

import com.angelfg.reportms.models.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReportHelper {

    @Value("${report.template}")
    private String reportTemplate;

    public String readTemplate(Company company) {
//        System.out.println(this.reportTemplate);
        return this.reportTemplate
                .replace("{company}", company.getName())
                .replace("{foundation_date}", company.getFoundationDate().toString())
                .replace("{founder}", company.getFounder())
                .replace("{web_sites}", company.getWebSites().toString());
    }

    public List<String> getPlaceholdersFromTemplate(String template) {
        String[] split = template.split("\\{");

        return Arrays.stream(split)
                .filter(line -> !line.isEmpty())
                .map(line -> {
                    int index = line.indexOf("}");
                    return line.substring(0, index);
                }).collect(Collectors.toList());
    }

}
