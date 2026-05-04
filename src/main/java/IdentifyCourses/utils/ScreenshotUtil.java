package IdentifyCourses.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + "/screenshots/";

    static {
        new File(SCREENSHOT_DIR).mkdirs();
    }

    public static String captureScreenshot(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = SCREENSHOT_DIR + fileName;

        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(filePath);
            FileUtils.copyFile(src, dest);
            System.out.println("[Screenshot] Saved: " + filePath);
        } catch (IOException e) {
            System.err.println("[Screenshot] Failed to save screenshot: " + e.getMessage());
            return null;
        }

        return filePath;
    }

    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.err.println("[Screenshot] Failed to capture bytes: " + e.getMessage());
            return new byte[0];
        }
    }
}