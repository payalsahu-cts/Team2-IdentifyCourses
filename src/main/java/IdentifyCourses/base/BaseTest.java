package IdentifyCourses.base;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

public class BaseTest {

    protected Properties config;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() throws IOException {
        config = loadConfig();
        String browser = config.getProperty("browser", "chrome");
        DriverFactory.initDriver(browser);

        WebDriver driver = DriverFactory.getDriver();
        driver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(Long.parseLong(config.getProperty("implicitWait", "10")))
        );

        wait = new WebDriverWait(driver,
            Duration.ofSeconds(Long.parseLong(config.getProperty("explicitWait", "20")))
        );

        driver.get(config.getProperty("baseURL"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(result.getName());
        }
        DriverFactory.quitDriver();
    }

    public void takeScreenshot(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver instanceof TakesScreenshot) {
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String destPath = config.getProperty("screenshotPath", "screenshots/")
                    + testName + "_" + timestamp + ".png";
                File dest = new File(destPath);
                dest.getParentFile().mkdirs();
                FileUtils.copyFile(src, dest);
                System.out.println("Screenshot saved: " + destPath);
            } catch (IOException e) {
                System.err.println("Failed to save screenshot: " + e.getMessage());
            }
        }
    }

    private Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        }
        return props;
    }
}
