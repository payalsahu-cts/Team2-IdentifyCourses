package IdentifyCourses.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;

public class GenerateTestData {

    public static void main(String[] args) throws Exception {
        String path = "testdata.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();

        // ── Header style ──────────────────────────────────────────────
        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont bold = wb.createFont();
        bold.setBold(true);
        headerStyle.setFont(bold);

        // ═══════════════════════════════════════════════════════════════
        // Sheet 1 — S1_Courses
        // ═══════════════════════════════════════════════════════════════
        XSSFSheet s1 = wb.createSheet("S1_Courses");
        String[] s1Headers = {"keyword", "level", "language", "count"};
        Object[] s1Data    = {"web development", "Beginner", "English", 2};

        Row h1 = s1.createRow(0);
        for (int i = 0; i < s1Headers.length; i++) {
            Cell c = h1.createCell(i);
            c.setCellValue(s1Headers[i]);
            c.setCellStyle(headerStyle);
        }
        Row r1 = s1.createRow(1);
        for (int i = 0; i < s1Data.length; i++) {
            Cell c = r1.createCell(i);
            if (s1Data[i] instanceof Integer) c.setCellValue((Integer) s1Data[i]);
            else c.setCellValue(s1Data[i].toString());
        }
        for (int i = 0; i < s1Headers.length; i++) s1.autoSizeColumn(i);

        // ═══════════════════════════════════════════════════════════════
        // Sheet 2 — S2_Languages
        // ═══════════════════════════════════════════════════════════════
        XSSFSheet s2 = wb.createSheet("S2_Languages");
        String[] s2Headers = {"keyword"};
        Object[] s2Data    = {"Language Learning"};

        Row h2 = s2.createRow(0);
        for (int i = 0; i < s2Headers.length; i++) {
            Cell c = h2.createCell(i);
            c.setCellValue(s2Headers[i]);
            c.setCellStyle(headerStyle);
        }
        Row r2 = s2.createRow(1);
        for (int i = 0; i < s2Data.length; i++) {
            r2.createCell(i).setCellValue(s2Data[i].toString());
        }
        for (int i = 0; i < s2Headers.length; i++) s2.autoSizeColumn(i);

        // ═══════════════════════════════════════════════════════════════
        // Sheet 3 — S3_Form
        // ═══════════════════════════════════════════════════════════════
        XSSFSheet s3 = wb.createSheet("S3_Form");
        String[] s3Headers = {"field", "value"};
        String[][] s3Data  = {
            {"firstName",       "Test"},
            {"lastName",        "Automation"},
            {"email",           "testuser@university.edu"},
            {"phone",           "9876543210"},
            {"institutionType", "University"},
            {"institutionName", "State University"},
            {"jobRole",         "Director"},
            {"department",      "Academic Affairs"},
            {"needs",           "Get in touch with sales"},
            {"country",         "India"},
            {"invalidEmail",    "testinvalid.com"}
        };

        Row h3 = s3.createRow(0);
        for (int i = 0; i < s3Headers.length; i++) {
            Cell c = h3.createCell(i);
            c.setCellValue(s3Headers[i]);
            c.setCellStyle(headerStyle);
        }
        for (int i = 0; i < s3Data.length; i++) {
            Row row = s3.createRow(i + 1);
            row.createCell(0).setCellValue(s3Data[i][0]);
            row.createCell(1).setCellValue(s3Data[i][1]);
        }
        for (int i = 0; i < s3Headers.length; i++) s3.autoSizeColumn(i);

        // ── Write file ────────────────────────────────────────────────
        File file = new File(path);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
        }
        wb.close();
        System.out.println("testdata.xlsx created at: " + file.getAbsolutePath());
    }
}
