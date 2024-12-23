package com.lms.controller;

import com.lms.domain.service.PerformanceAnalyticsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class PerformanceAnalyticsController {

    private final PerformanceAnalyticsService analyticsService;

    public PerformanceAnalyticsController(PerformanceAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/performance")
    public ResponseEntity<ByteArrayResource> generatePerformanceReport(
            @RequestParam(required = false) Long assignmentId) {
        try {
            byte[] reportData = analyticsService.generatePerformanceReport(assignmentId);

            ByteArrayResource resource = new ByteArrayResource(reportData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=performance_report" +
                                    (assignmentId != null ? "_assignment_" + assignmentId : "") +
                                    ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}