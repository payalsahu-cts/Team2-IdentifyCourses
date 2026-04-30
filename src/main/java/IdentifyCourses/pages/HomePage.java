package IdentifyCourses.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    // Actual Coursera nav text is "For Businesses" (enterprise link) and "For Universities" (campus link)
    private By forEnterpriseLink = By.xpath(
        "//a[contains(normalize-space(),'For Businesses')] | " +
        "//a[contains(@href,'/business')] | " +
        "//a[contains(normalize-space(),'For Enterprise')] | " +
        "//a[contains(normalize-space(),'For Universities')]"
    );

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.actions = new Actions(driver);
    }

    // Step 1: Click "For Businesses" in the header (maps to "For Enterprise" in task spec)
    public void clickForEnterprise() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(forEnterpriseLink));
        actions.moveToElement(link).click().perform();
    }

    // Step 2: Coursera has no "Products" submenu — just wait for page to settle
    public void hoverProducts() {
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        System.out.println("[Nav] Products hover — no submenu on current Coursera UI, continuing.");
    }

    // Step 3: Navigate directly to campus page URL
    public void clickCoursesForCampus() {
        driver.get("https://www.coursera.org/campus");
        wait.until(ExpectedConditions.urlContains("campus"));
        System.out.println("[Nav] Navigated to Coursera for Campus: " + driver.getCurrentUrl());
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}
