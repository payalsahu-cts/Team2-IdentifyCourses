package IdentifyCourses.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {}

    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (ExtentManager.class) {
                if (extent == null) {
                    extent = createInstance();
                }
            }
        }
        return extent;
    }

    private static ExtentReports createInstance() {
        String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
        new File(System.getProperty("user.dir") + "/reports").mkdirs();

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Team2 - Identify Courses Report");
        sparkReporter.config().setReportName("Coursera Automation Test Report");
        sparkReporter.config().setEncoding("utf-8");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Project", "Team2 - IdentifyCourses");
        reports.setSystemInfo("Browser", "Chrome");
        reports.setSystemInfo("Base URL", "https://www.coursera.org");
        reports.setSystemInfo("Author", "Team2");
        return reports;
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}