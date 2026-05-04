package IdentifyCourses.stepDefs;

import IdentifyCourses.base.DriverFactory;
import IdentifyCourses.utils.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenshotHooks {

    private static final ExtentReports extent = ExtentManager.getInstance();

    // ThreadLocal — each parallel thread gets its own ExtentTest and step counter
    private static final ThreadLocal<ExtentTest>    extentTest  = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> stepCounter =
            ThreadLocal.withInitial(() -> new AtomicInteger(0));

    // Runs FIRST (order=0) before driver is initialised
    @Before(order = 0)
    public void startReport(Scenario scenario) {
        ExtentTest test = extent.createTest(
                scenario.getName(),
                "Tags: " + scenario.getSourceTagNames()
        );
        extentTest.set(test);
        stepCounter.get().set(0);
        test.log(Status.INFO, "Scenario started: <b>" + scenario.getName() + "</b>");
    }

    // Fires after EVERY Gherkin step
    @AfterStep
    public void captureStepScreenshot(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) return;

        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            String stepLabel  = "Step-" + stepCounter.get().incrementAndGet();
            String timestamp  = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String scenarioId = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName   = scenarioId + "_" + stepLabel + "_" + timestamp + ".png";

            // Save PNG to screenshots/ folder
            File dest = new File("screenshots/" + fileName);
            dest.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(dest, screenshotBytes);
            System.out.println("[Screenshot] " + stepLabel + " -> " + dest.getPath());

            // Attach to Cucumber HTML report inline per step
            scenario.attach(screenshotBytes, "image/png", stepLabel);

            // Log to Extent Report with embedded screenshot
            String base64 = Base64.getEncoder().encodeToString(screenshotBytes);
            ExtentTest test = extentTest.get();
            if (test != null) {
                Status status = scenario.isFailed() ? Status.FAIL : Status.PASS;
                test.log(status, stepLabel,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            }

        } catch (Exception e) {
            System.err.println("[Screenshot] AfterStep capture failed: " + e.getMessage());
        }
    }

    // Runs LAST (order=0) after driver is quit — flushes report to disk
    @After(order = 0)
    public void finishReport(Scenario scenario) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            if (scenario.isFailed()) {
                test.log(Status.FAIL, "Scenario <b>FAILED</b>: " + scenario.getName());
            } else {
                test.log(Status.PASS, "Scenario <b>PASSED</b>: " + scenario.getName());
            }
        }
        extent.flush();
        extentTest.remove();
        stepCounter.remove();
    }
}
