package IdentifyCourses.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class ExcelUtils {

    /**
     * Writes Language Learning filter data to an Excel file with two sheets:
     *   Sheet 1 — "Languages"        : Language Name | Course Count
     *   Sheet 2 — "Difficulty Levels": Level Name    | Course Count
     * A bold totals row is appended to each sheet.
     */
    public static void writeLanguageLearningToExcel(
            String filePath,
            LinkedHashMap<String, Integer> languages,
            LinkedHashMap<String, Integer> levels) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle totalStyle  = buildTotalStyle(workbook);

            writeFilterSheet(workbook, "Languages",         "Language",
                             languages, headerStyle, totalStyle);
            writeFilterSheet(workbook, "Difficulty Levels", "Level",
                             levels,   headerStyle, totalStyle);

            saveWorkbook(workbook, filePath);
            System.out.println("[ExcelUtils] language_learning.xlsx saved → " + filePath);
        }
    }

    private static void writeFilterSheet(Workbook wb, String sheetName, String colHeader,
                                         LinkedHashMap<String, Integer> data,
                                         CellStyle headerStyle, CellStyle totalStyle) {
        Sheet sheet = wb.createSheet(sheetName);

        // Header row
        Row header = sheet.createRow(0);
        createCell(header, 0, colHeader,      headerStyle);
        createCell(header, 1, "Course Count", headerStyle);

        // Data rows
        int rowIdx = 1;
        int total  = 0;
        for (java.util.Map.Entry<String, Integer> entry : data.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            total += entry.getValue();
        }

        // Totals row
        Row totalsRow = sheet.createRow(rowIdx);
        createCell(totalsRow, 0,
                   "TOTAL (" + data.size() + " " + colHeader.toLowerCase() + "s)",
                   totalStyle);
        Cell totalCell = totalsRow.createCell(1);
        totalCell.setCellValue(total);
        totalCell.setCellStyle(totalStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private static CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle buildTotalStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void saveWorkbook(Workbook workbook, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
    }

    // ── Task 1 — unchanged ────────────────────────────────────────────────────

    public static void writeCoursesToExcel(String filePath, List<String[]> courseData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Web Dev Courses");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"Course Name", "Total Learning Hours", "Rating"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < courseData.size(); i++) {
                Row row = sheet.createRow(i + 1);
                String[] rowData = courseData.get(i);
                for (int j = 0; j < rowData.length; j++) {
                    row.createCell(j).setCellValue(rowData[j] != null ? rowData[j] : "N/A");
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("[ExcelUtils] Saved " + courseData.size()
                    + " course(s) to: " + file.getAbsolutePath());
        }
    }
}
