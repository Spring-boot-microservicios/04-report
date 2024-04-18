package com.angelfg.reportms.services;

import com.angelfg.reportms.helpers.ReportHelper;
import com.angelfg.reportms.models.Company;
import com.angelfg.reportms.models.WebSite;
import com.angelfg.reportms.repositories.CompaniesFallbackRepository;
import com.angelfg.reportms.repositories.CompaniesRepository;
import com.angelfg.reportms.streams.ReportPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final CompaniesRepository companiesRepository;
    private final ReportHelper reportHelper;

    private final CompaniesFallbackRepository companiesFallbackRepository;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    public final ReportPublisher reportPublisher;

    @Override
    public String makeReport(String name) {
//        return this.reportHelper.readTemplate(this.companiesRepository.getByName(name).orElseThrow());
//        return this.companiesRepository.getByName(name).orElseThrow().getName();
        CircuitBreaker circuitBreaker = this.circuitBreakerFactory.create("companies-circuitbreaker");
        return circuitBreaker.run(
            () -> this.makeReportMain(name),
            throwable -> this.makeReportFallback(name, throwable)
        );
    }

    @Override
    public String saveReport(String report) {
//        Company company = Company.builder()
//                .name("test")
//                .logo("logo")
//                .founder("test")
//                .foundationDate(LocalDate.now())
//                .webSites(List.of())
//                .build();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<String> placeholder = this.reportHelper.getPlaceholdersFromTemplate(report);

        List<WebSite> webSites = Stream.of(placeholder.get(3))
                .map(website -> WebSite.builder().name(website).build())
                .toList();

        Company company = Company.builder()
                .name(placeholder.get(0))
                .foundationDate(LocalDate.parse(placeholder.get(1), format))
                .founder(placeholder.get(2))
                .webSites(webSites)
                .build();

        // Generamos el publisher
        this.reportPublisher.publishReport(report);

        this.companiesRepository.postByName(company);
        return "Saved";
    }

    @Override
    public void deleteReport(String name) {
        this.companiesRepository.deleteByName(name);
    }

    private String makeReportMain(String name) {
        return this.reportHelper.readTemplate(this.companiesRepository.getByName(name).orElseThrow());
    }

    private String makeReportFallback(String name, Throwable error) {
        log.warn("Error {}", error.getMessage());
        return this.reportHelper.readTemplate(this.companiesFallbackRepository.getByName(name));
    }

}
