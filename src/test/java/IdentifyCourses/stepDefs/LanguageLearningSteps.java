package IdentifyCourses.stepDefs;

import IdentifyCourses.base.DriverFactory;
import IdentifyCourses.pages.LanguageLearningPage;
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
import java.util.LinkedHashMap;
import java.util.Properties;

public class LanguageLearningSteps {

    private LanguageLearningPage page;
    private LinkedHashMap<String, Integer> languageOptions;
    private LinkedHashMap<String, Integer> levelOptions;

    @Before("@languagelearning")
    public void setUp() throws IOException {
        Properties config = loadConfig();
        DriverFactory.initDriver(config.getProperty("browser", "chrome"));
        WebDriver driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(config.getProperty("implicitWait", "10")))
        );
        page = new LanguageLearningPage();
    }

    @After("@languagelearning")
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

    @Given("I open the Coursera website for language learning research")
    public void iOpenTheCourseraWebsite() throws IOException {
        String baseUrl = loadConfig().getProperty("baseURL", "https://www.coursera.org");
        DriverFactory.getDriver().get(baseUrl);
        System.out.println("[Step] Navigated to: " + baseUrl);
    }

    @When("I search for {string} courses on Coursera")
    public void iSearchForCoursesOnCoursera(String keyword) {
        System.out.println("[Step] Searching for: " + keyword);
        page.searchForLanguageLearning();
    }

    @Then("Language Learning search results should be displayed on the page")
    public void languageLearningResultsShouldBeDisplayed() {
        Assert.assertTrue(page.areResultsDisplayed(),
                "Expected Language Learning search results to be visible.");
        System.out.println("[Step] Language Learning results confirmed visible.");
    }

    @When("I extract all available language filter options with their course counts")
    public void iExtractAllLanguageFilterOptions() {
        System.out.println("[Step] Extracting all language filter options...");
        languageOptions = page.extractLanguageOptions();
        Assert.assertFalse(languageOptions.isEmpty(),
                "No language filter options were extracted.");
    }

    @And("I extract all available difficulty level filter options with their course counts")
    public void iExtractAllLevelFilterOptions() {
        System.out.println("[Step] Extracting all level filter options...");
        levelOptions = page.extractLevelOptions();
        Assert.assertFalse(levelOptions.isEmpty(),
                "No level filter options were extracted.");
    }

    @Then("the language and level distribution data is printed to the console")
    public void printLanguageAndLevelData() {
        printSection("LANGUAGE LEARNING — LANGUAGES AVAILABLE", "Language", languageOptions);
        printSection("LANGUAGE LEARNING — DIFFICULTY LEVELS",   "Level",    levelOptions);
    }

    @And("the language and level data is exported to Excel file {string}")
    public void exportToExcel(String filePath) throws IOException {
        ExcelUtils.writeLanguageLearningToExcel(filePath, languageOptions, levelOptions);
        File file = new File(filePath);
        Assert.assertTrue(file.exists(),
                "Excel file was not created at: " + file.getAbsolutePath());
        System.out.println("[Step] Excel file verified at: " + file.getAbsolutePath());
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private void printSection(String title, String colHeader,
                              LinkedHashMap<String, Integer> data) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("  " + title);
        System.out.println("=".repeat(65));
        System.out.printf("%-40s %10s%n", colHeader, "Course Count");
        System.out.println("-".repeat(65));
        int total = 0;
        for (java.util.Map.Entry<String, Integer> entry : data.entrySet()) {
            System.out.printf("%-40s %,10d%n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("-".repeat(65));
        System.out.printf("%-40s %,10d%n",
                "TOTAL  (" + data.size() + " " + colHeader.toLowerCase() + "s)", total);
        System.out.println("=".repeat(65));
    }

    private Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) props.load(is);
        }
        return props;
    }
}
