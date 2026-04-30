package IdentifyCourses.stepDefs;

import IdentifyCourses.base.DriverFactory;
import IdentifyCourses.pages.CoursesSearchPage;
import IdentifyCourses.utils.ExcelUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CourseSearchSteps {

    private CoursesSearchPage searchPage;
    private List<String[]> extractedCourses;

    // Hooks scoped to @webdev — won't interfere with other members' scenarios
    @Before("@webdev")
    public void setUp() throws IOException {
        Properties config = loadConfig();
        DriverFactory.initDriver(config.getProperty("browser", "chrome"));
        WebDriver driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(config.getProperty("implicitWait", "10")))
        );
        searchPage = new CoursesSearchPage();
    }

    @After("@webdev")
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) return;

        if (scenario.isFailed()) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", scenario.getName());
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String path = "screenshots/" + scenario.getName().replaceAll("\\s+", "_")
                        + "_" + timestamp + ".png";
                File dest = new File(path);
                dest.getParentFile().mkdirs();
                FileUtils.writeByteArrayToFile(dest, screenshot);
                System.out.println("[Screenshot] Saved: " + dest.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[Screenshot] Failed: " + e.getMessage());
            }
        }

        DriverFactory.quitDriver();
    }

    // ── Step Definitions ──────────────────────────────────────────────────────

    @Given("I am on the Coursera homepage")
    public void iAmOnTheCourseraHomepage() throws IOException {
        String baseUrl = loadConfig().getProperty("baseURL", "https://www.coursera.org");
        DriverFactory.getDriver().get(baseUrl);
        System.out.println("[Step] Navigated to: " + baseUrl);
    }

    @When("I search for {string}")
    public void iSearchFor(String keyword) {
        System.out.println("[Step] Searching for: " + keyword);
        searchPage.searchFor(keyword);
    }

    @And("I apply the {string} level filter")
    public void iApplyTheLevelFilter(String level) {
        System.out.println("[Step] Applying Level filter: " + level);
        searchPage.applyLevelFilter(level);
    }

    @And("I apply the {string} language filter")
    public void iApplyTheLanguageFilter(String language) {
        System.out.println("[Step] Applying Language filter: " + language);
        searchPage.applyLanguageFilter(language);
    }

    @Then("I should see course results on the page")
    public void iShouldSeeCourseResultsOnThePage() {
        Assert.assertTrue(searchPage.areResultsDisplayed(),
                "Expected course results to be displayed after applying filters.");
        System.out.println("[Step] Course results confirmed visible.");
    }

    @When("I extract details from the first {int} course results")
    public void iExtractDetailsFromTheFirstNCourseResults(int count) {
        System.out.println("[Step] Extracting details from first " + count + " course(s)...");
        extractedCourses = searchPage.extractCourseDetails(count);
        Assert.assertFalse(extractedCourses.isEmpty(), "No course details were extracted.");
    }

    @Then("the extracted course details are printed to the console")
    public void theExtractedCourseDetailsArePrintedToTheConsole() {
        System.out.println("\n========== Extracted Web Development Courses ==========");
        System.out.printf("%-5s %-70s %-30s %-10s%n", "No.", "Course Name", "Hours", "Rating");
        System.out.println("-".repeat(120));
        for (int i = 0; i < extractedCourses.size(); i++) {
            String[] c = extractedCourses.get(i);
            System.out.printf("%-5d %-70s %-30s %-10s%n",
                    i + 1, truncate(c[0], 68), truncate(c[1], 28), c[2]);
        }
        System.out.println("=".repeat(120));
    }

    @And("the course data is written to Excel file {string}")
    public void theCourseDataIsWrittenToExcelFile(String filePath) throws IOException {
        ExcelUtils.writeCoursesToExcel(filePath, extractedCourses);
        File file = new File(filePath);
        Assert.assertTrue(file.exists(),
                "Excel file was not created at: " + file.getAbsolutePath());
        System.out.println("[Step] Excel file verified at: " + file.getAbsolutePath());
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) props.load(is);
        }
        return props;
    }

    private String truncate(String s, int max) {
        if (s == null) return "N/A";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
