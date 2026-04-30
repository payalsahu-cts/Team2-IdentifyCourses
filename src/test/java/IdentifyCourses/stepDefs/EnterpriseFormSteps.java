package IdentifyCourses.stepDefs;

import IdentifyCourses.base.DriverFactory;
import IdentifyCourses.pages.EnterpriseCampusPage;
import IdentifyCourses.pages.HomePage;
import io.cucumber.datatable.DataTable;
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
import java.util.Map;
import java.util.Properties;

public class EnterpriseFormSteps {

    private HomePage homePage;
    private EnterpriseCampusPage campusPage;
    private String capturedErrorMessage;

    // Hooks scoped to @enterprise — won't interfere with other members' scenarios
    @Before("@enterprise")
    public void setUp() throws IOException {
        Properties config = loadConfig();
        DriverFactory.initDriver(config.getProperty("browser", "chrome"));
        WebDriver driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(config.getProperty("implicitWait", "10")))
        );
        homePage = new HomePage(driver);
        campusPage = new EnterpriseCampusPage(driver);
    }

    @After("@enterprise")
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
                System.out.println("[Screenshot] Saved on failure: " + dest.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[Screenshot] Failed to capture: " + e.getMessage());
            }
        }

        DriverFactory.quitDriver();
    }

    // ── Step Definitions ──────────────────────────────────────────────────────

    @When("I click {string} in the header navigation")
    public void iClickInTheHeaderNavigation(String menuItem) {
        System.out.println("[Step] Clicking header nav item: " + menuItem);
        if (menuItem.equalsIgnoreCase("For Enterprise")) {
            homePage.clickForEnterprise();
        }
    }

    @And("I hover over the {string} menu")
    public void iHoverOverTheMenu(String menuItem) {
        System.out.println("[Step] Hovering over menu: " + menuItem);
        if (menuItem.equalsIgnoreCase("Products")) {
            homePage.hoverProducts();
        }
    }

    @And("I click {string}")
    public void iClick(String linkText) {
        System.out.println("[Step] Clicking link: " + linkText);
        if (linkText.equalsIgnoreCase("Courses for Campus")) {
            homePage.clickCoursesForCampus();
        }
    }

    @Then("I should be on the Courses for Campus page")
    public void iShouldBeOnTheCoursesForCampusPage() {
        Assert.assertTrue(campusPage.isOnCampusPage(),
                "Expected to be on the Courses for Campus page. Current URL: " + campusPage.getCurrentUrl());
        System.out.println("[Step] Confirmed on Campus page: " + campusPage.getCurrentUrl());
    }

    @And("I scroll to the {string} form section")
    public void iScrollToTheFormSection(String sectionName) {
        System.out.println("[Step] Scrolling to form section: " + sectionName);
        campusPage.scrollToReadyToTransformForm();
    }

    @And("I fill the form with the following valid details:")
    public void iFillTheFormWithValidDetails(DataTable dataTable) {
        System.out.println("[Step] Filling form fields with valid test data...");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String field = row.get("field");
            String value = row.get("value");
            System.out.println("  -> " + field + " = " + value);
            switch (field) {
                case "firstName":       campusPage.fillFirstName(value);       break;
                case "lastName":        campusPage.fillLastName(value);        break;
                case "email":           campusPage.fillEmail(value);           break;
                case "phone":           campusPage.fillPhone(value);           break;
                case "institutionType": campusPage.fillInstitutionType(value); break;
                case "institutionName": campusPage.fillInstitutionName(value); break;
                case "jobRole":         campusPage.fillJobRole(value);         break;
                case "department":      campusPage.fillDepartment(value);      break;
                case "needs":           campusPage.fillNeeds(value);           break;
                case "country":         campusPage.fillCountry(value);         break;
                default:
                    System.out.println("  [WARN] Unknown field: " + field + ", skipping.");
            }
        }
    }

    @And("I replace the email field with invalid email {string}")
    public void iReplaceEmailWithInvalidEmail(String invalidEmail) {
        System.out.println("[Step] Entering invalid email: " + invalidEmail);
        campusPage.clearAndFillEmail(invalidEmail);
    }

    @And("I click the Submit button")
    public void iClickTheSubmitButton() {
        System.out.println("[Step] Clicking the Submit button...");
        campusPage.clickSubmit();
    }

    @Then("the form submission result is displayed")
    public void theFormSubmissionResultIsDisplayed() {
        System.out.println("[Step] Form submitted. Current page: " + campusPage.getCurrentUrl());
    }

    @Then("a validation error message is displayed for the email field")
    public void aValidationErrorMessageIsDisplayedForEmailField() {
        capturedErrorMessage = campusPage.captureEmailErrorMessage();
        Assert.assertNotNull(capturedErrorMessage, "Expected a validation error message but none was found.");
        Assert.assertFalse(capturedErrorMessage.isEmpty(),
                "Expected a non-empty validation error message for the email field.");
        System.out.println("[Step] Email validation error confirmed.");
    }

    @And("the error message text is captured and printed to the console")
    public void theErrorMessageTextIsCapturedAndPrinted() {
        System.out.println("\n========== Enterprise Form Email Validation Error ==========");
        System.out.println("  Error Message: " + capturedErrorMessage);
        System.out.println("============================================================\n");
    }

    @And("a screenshot of the error state is saved")
    public void aScreenshotOfTheErrorStateIsSaved() {
        WebDriver driver = DriverFactory.getDriver();
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String path = "screenshots/enterprise_form_error_" + timestamp + ".png";
            File dest = new File(path);
            dest.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(dest, screenshot);
            System.out.println("[Screenshot] Error state saved at: " + dest.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[Screenshot] Failed to save error state screenshot: " + e.getMessage());
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) props.load(is);
        }
        return props;
    }
}
