package IdentifyCourses.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EnterpriseCampusPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // "Ready to transform" form section — broad match since section text may vary
    private By readyToTransformSection = By.xpath(
        "//*[contains(text(),'Ready to transform')] | " +
        "//*[contains(text(),'Get in touch')] | " +
        "//*[contains(text(),'Contact us')] | " +
        "//*[contains(text(),'Request info')]"
    );

    // Actual form fields matching the real Coursera for Campus form (verified from UI)
    private By firstNameField = By.xpath(
        "//input[contains(@placeholder,'First Name')] | //input[@name='firstName'] | //input[@id='firstName']"
    );

    private By lastNameField = By.xpath(
        "//input[contains(@placeholder,'Last Name')] | //input[@name='lastName'] | //input[@id='lastName']"
    );

    private By emailField = By.xpath(
        "//input[contains(@placeholder,'Work Email')] | //input[@type='email'] | " +
        "//input[@name='email'] | //input[contains(@placeholder,'Email')]"
    );

    private By phoneField = By.xpath(
        "//input[contains(@placeholder,'Phone Number')] | //input[@type='tel'] | " +
        "//input[@name='phone'] | //input[contains(@placeholder,'Phone')]"
    );

    private By institutionTypeField = By.xpath(
        "//input[contains(@placeholder,'Institution Type')] | //input[@name='institutionType'] | " +
        "//select[contains(@placeholder,'Institution Type')] | //select[@name='institutionType']"
    );

    private By institutionNameField = By.xpath(
        "//input[contains(@placeholder,'Institution Name')] | //input[@name='institutionName'] | " +
        "//input[@name='company'] | //input[contains(@placeholder,'Institution')]"
    );

    private By jobRoleField = By.xpath(
        "//input[contains(@placeholder,'Job Role')] | //input[@name='jobRole'] | " +
        "//input[@name='jobTitle'] | //input[contains(@placeholder,'Job')]"
    );

    private By departmentField = By.xpath(
        "//input[contains(@placeholder,'Department')] | //input[@name='department']"
    );

    private By needsField = By.xpath(
        "//input[contains(@placeholder,'Which best describes')] | " +
        "//select[contains(@placeholder,'Which best describes')] | " +
        "//input[@name='needs'] | //select[@name='needs']"
    );

    private By countryField = By.xpath(
        "//input[contains(@placeholder,'Country')] | //select[@name='country'] | " +
        "//select[@id='country'] | //input[@name='country']"
    );

    private By submitButton = By.xpath(
        "//button[normalize-space()='Submit'] | //button[@type='submit'] | //input[@type='submit']"
    );

    // Error message locators
    private By emailErrorMessage = By.xpath(
        "//*[@role='alert'] | " +
        "//span[contains(@class,'error') or contains(@class,'invalid')] | " +
        "//div[contains(@class,'error') or contains(@class,'invalid')] | " +
        "//p[contains(@class,'error') or contains(@class,'invalid')]"
    );

    public EnterpriseCampusPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public void scrollToReadyToTransformForm() {
        try {
            WebElement section = wait.until(ExpectedConditions.presenceOfElementLocated(readyToTransformSection));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", section);
            Thread.sleep(1200);
            System.out.println("[Scroll] Scrolled to form section via element.");
        } catch (Exception e) {
            // Fallback: scroll toward bottom where contact forms typically live
            js.executeScript("window.scrollTo({top: document.body.scrollHeight * 0.80, behavior: 'smooth'});");
            try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
            System.out.println("[Scroll] Scrolled to 80% of page (fallback).");
        }
    }

    private void fillField(By locator, String value, String fieldName) {
        try {
            WebElement field = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", field);
            field.clear();
            field.sendKeys(value);
        } catch (Exception e) {
            System.out.println("[INFO] Field '" + fieldName + "' not found or not interactable, skipping.");
        }
    }

    public void fillFirstName(String value)       { fillField(firstNameField,      value, "First Name"); }
    public void fillLastName(String value)        { fillField(lastNameField,       value, "Last Name"); }
    public void fillPhone(String value)           { fillField(phoneField,          value, "Phone Number"); }
    public void fillInstitutionType(String value) { fillField(institutionTypeField, value, "Institution Type"); }
    public void fillInstitutionName(String value) { fillField(institutionNameField, value, "Institution Name"); }
    public void fillJobRole(String value)         { fillField(jobRoleField,        value, "Job Role"); }
    public void fillDepartment(String value)      { fillField(departmentField,     value, "Department"); }
    public void fillNeeds(String value)           { fillField(needsField,          value, "Which best describes your needs"); }

    public void fillEmail(String value) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(emailField));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", field);
        field.clear();
        field.sendKeys(value);
    }

    public void clearAndFillEmail(String value) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(emailField));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", field);
        field.clear();
        js.executeScript("arguments[0].value = '';", field);
        field.sendKeys(value);
    }

    public void fillCountry(String value) {
        try {
            WebElement field = wait.until(ExpectedConditions.elementToBeClickable(countryField));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", field);
            // Try as select dropdown first, then as text input
            try { new Select(field).selectByVisibleText(value); }
            catch (Exception ex) { field.clear(); field.sendKeys(value); }
        } catch (Exception e) {
            System.out.println("[INFO] Country field not found, skipping.");
        }
    }

    public void clickSubmit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
    }

    public String captureEmailErrorMessage() {
        try {
            WebElement errorEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailErrorMessage));
            return errorEl.getText().trim();
        } catch (Exception e) {
            try {
                WebElement emailInput = driver.findElement(emailField);
                String msg = (String) js.executeScript("return arguments[0].validationMessage;", emailInput);
                if (msg != null && !msg.isEmpty()) return msg;
            } catch (Exception ex) {
                System.out.println("[WARN] Could not retrieve validation message via JS.");
            }
            return "[No error message captured]";
        }
    }

    public boolean isOnCampusPage() {
        return driver.getCurrentUrl().contains("campus") || driver.getTitle().toLowerCase().contains("campus");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
