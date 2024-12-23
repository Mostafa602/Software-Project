package com.lms.domain.service;

import com.lms.domain.model.course.AssignmentSubmission;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.lms.domain.repository.AssignmentSubmissionRepository;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

@Service
public class PerformanceAnalyticsService {

    private final AssignmentSubmissionRepository submissionRepository;

    public PerformanceAnalyticsService(AssignmentSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public byte[] generatePerformanceReport(Long assignmentId) throws IOException {
        try {
            List<AssignmentSubmission> submissions;
            if (assignmentId != null) {
                submissions = submissionRepository.findAll().stream()
                        .filter(s -> s.getAssignment().getId().equals(assignmentId))
                        .collect(Collectors.toList());
            } else {
                submissions = submissionRepository.findAll();
            }

            submissions = submissions.stream()
                    .filter(s -> s.getGrade() != -1)
                    .collect(Collectors.toList());

            if (submissions.isEmpty()) {
                throw new IllegalStateException("No graded submissions found");
            }

            XSSFWorkbook workbook = new XSSFWorkbook();

            // Create sheets
            createGradesSheet(workbook.createSheet("Grades Report"), submissions);
            createStatisticsSheet(workbook.createSheet("Statistics"), submissions);
            createGradeDistributionChart((XSSFSheet) workbook.createSheet("Grade Distribution"), submissions);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();

            return bos.toByteArray();
        } catch (Exception e) {
            throw new IOException("Failed to generate report: " + e.getMessage(), e);
        }
    }

    private void createGradeDistributionChart(XSSFSheet sheet, List<AssignmentSubmission> submissions) {
        try {
            // Calculate grade distribution
            Map<String, Long> distribution = submissions.stream()
                    .collect(Collectors.groupingBy(
                            s -> calculateLetterGrade(s.getGrade()),
                            Collectors.counting()
                    ));

            // Create data table
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Grade");
            headerRow.createCell(1).setCellValue("Count");

            // Create data rows in specific order
            String[] grades = {"A", "B", "C", "D", "F"};
            for (int i = 0; i < grades.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(grades[i]);
                Cell valueCell = row.createCell(1);
                valueCell.setCellValue(distribution.getOrDefault(grades[i], 0L).doubleValue());
            }

            // Create the chart
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, 1, 15, 15);

            XSSFChart chart = drawing.createChart(anchor);

            // Create axis before creating data
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("Grades");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Number of Students");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
            leftAxis.setNumberFormat("#,##0");

            // Create data sources
            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                    new CellRangeAddress(1, 5, 0, 0));
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(1, 5, 1, 1));

            // Create chart data
            XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setVaryColors(true);

            // Add series to the chart
            XDDFChartData.Series series = data.addSeries(categories, values);
            series.setTitle("Number of Students", null);

            // Plot the data
            chart.plot(data);

            // Set the bar direction
            XDDFBarChartData barData = (XDDFBarChartData) data;
            barData.setBarDirection(BarDirection.COL);

            // Set chart title
            chart.setTitleText("Grade Distribution");
            chart.setTitleOverlay(false);

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

        } catch (Exception e) {
            System.err.println("Error creating chart: " + e.getMessage());
            e.printStackTrace();

            // Add error message to sheet
            Row errorRow = sheet.createRow(0);
            errorRow.createCell(0).setCellValue("Error creating chart: " + e.getMessage());
        }
    }

    private void createGradesSheet(Sheet sheet, List<AssignmentSubmission> submissions) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student ID");
        headerRow.createCell(1).setCellValue("Student Name");
        headerRow.createCell(2).setCellValue("Assignment ID");
        headerRow.createCell(3).setCellValue("Assignment Title");
        headerRow.createCell(4).setCellValue("Grade");
        headerRow.createCell(5).setCellValue("Letter Grade");
        headerRow.createCell(6).setCellValue("Submission URL");

        int rowNum = 1;
        for (AssignmentSubmission submission : submissions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(submission.getStudent().getId());
            row.createCell(1).setCellValue(submission.getStudent().getFullName());
            row.createCell(2).setCellValue(submission.getAssignment().getId());
            row.createCell(3).setCellValue(submission.getAssignment().getTitle());
            row.createCell(4).setCellValue(submission.getGrade());
            row.createCell(5).setCellValue(calculateLetterGrade(submission.getGrade()));
            row.createCell(6).setCellValue(submission.getUrl());
        }

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createStatisticsSheet(Sheet sheet, List<AssignmentSubmission> submissions) {
        if (submissions.isEmpty()) {
            createRow(sheet, 0, "No graded submissions found", "");
            return;
        }

        DoubleSummaryStatistics stats = submissions.stream()
                .mapToDouble(AssignmentSubmission::getGrade)
                .summaryStatistics();

        int rowNum = 0;

        createRow(sheet, rowNum++, "Class Average", String.format("%.2f", stats.getAverage()));
        createRow(sheet, rowNum++, "Highest Grade", String.format("%.2f", stats.getMax()));
        createRow(sheet, rowNum++, "Lowest Grade", String.format("%.2f", stats.getMin()));
        createRow(sheet, rowNum++, "Total Graded Submissions", String.valueOf(stats.getCount()));

        long passCount = submissions.stream()
                .filter(s -> s.getGrade() >= 60.0)
                .count();
        createRow(sheet, rowNum++, "Passed Students", String.valueOf(passCount));
        createRow(sheet, rowNum++, "Failed Students", String.valueOf(submissions.size() - passCount));

        rowNum++;
        createRow(sheet, rowNum++, "Grade Distribution", "");
        Map<String, Long> distribution = submissions.stream()
                .collect(Collectors.groupingBy(
                        s -> calculateLetterGrade(s.getGrade()),
                        Collectors.counting()
                ));

        for (String grade : Arrays.asList("A", "B", "C", "D", "F")) {
            createRow(sheet, rowNum++, grade, String.valueOf(distribution.getOrDefault(grade, 0L)));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private String calculateLetterGrade(float grade) {
        if (grade >= 90) return "A";
        if (grade >= 80) return "B";
        if (grade >= 70) return "C";
        if (grade >= 60) return "D";
        return "F";
    }

    private void createRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        Cell valueCell = row.createCell(1);

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);

        labelCell.setCellStyle(style);
        valueCell.setCellStyle(style);

        labelCell.setCellValue(label);
        valueCell.setCellValue(value);
    }
}