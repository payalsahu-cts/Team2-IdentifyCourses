package IdentifyCourses.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;

public class EnterpriseCampusPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // Section anchor — broad match for the contact/request form heading
    private By readyToTransformSection = By.xpath(
        "//*[contains(text(),'Ready to transform')] | " +
        "//*[contains(text(),'Get in touch')] | " +
        "//*[contains(text(),'Contact us')] | " +
        "//*[contains(text(),'Request info')]"
    );

    // Marketo form — exact IDs from the rendered HTML on coursera.org/campus
    private By firstNameField       = By.id("FirstName");
    private By lastNameField        = By.id("LastName");
    private By emailField           = By.id("Email");
    private By phoneField           = By.id("Phone");
    private By institutionTypeSelect = By.id("Institution_Type__c");  // <select>
    private By institutionNameField  = By.id("Company");
    private By jobRoleSelect        = By.id("Title");                  // <select>
    private By departmentSelect     = By.id("Department");             // <select>
    private By needsSelect          = By.id("Self_Reported_Needs__c"); // <select>
    private By countrySelect        = By.id("Country");               // <select>

    private By submitButton = By.cssSelector("button.mktoButton[type='submit'], button[type='submit']");

    // Error message locators — covers both ARIA alerts and Marketo inline validation
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

    private void selectDropdownOption(By locator, String value, String fieldName) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            Select select = new Select(el);
            try {
                select.selectByVisibleText(value);
                System.out.println("[Dropdown] '" + fieldName + "' set to: " + value);
            } catch (NoSuchElementException e) {
                // Partial case-insensitive match
                select.getOptions().stream()
                    .filter(o -> o.getText().toLowerCase().contains(value.toLowerCase()))
                    .findFirst()
                    .ifPresentOrElse(
                        o -> {
                            select.selectByVisibleText(o.getText());
                            System.out.println("[Dropdown] '" + fieldName + "' partial-matched '" + value + "' -> '" + o.getText() + "'");
                        },
                        () -> System.out.println("[WARN] Dropdown '" + fieldName + "': no option containing '" + value + "' found, skipping.")
                    );
            }
        } catch (Exception e) {
            System.out.println("[INFO] Dropdown '" + fieldName + "' not found or not interactable, skipping. (" + e.getMessage() + ")");
        }
    }

    public void fillFirstName(String value)       { fillField(firstNameField,      value, "First Name"); }
    public void fillLastName(String value)         { fillField(lastNameField,       value, "Last Name"); }
    public void fillPhone(String value)            { fillField(phoneField,          value, "Phone Number"); }
    public void fillInstitutionName(String value)  { fillField(institutionNameField, value, "Institution Name"); }

    /** id="Institution_Type__c" — options: University/4 Year College, 2 Year College, Graduate or Professional School, Ministry of Education, Other */
    public void fillInstitutionType(String value) {
        selectDropdownOption(institutionTypeSelect, value, "Institution Type");
    }

    /** id="Title" — options: President/Provost, Chancellor/Rector, Vice-Chancellor/Vice-Rector, Vice-President/Vice-Provost, Registrar, CEO, COO/CIO, Dean, Department Head, Director, Professor, Student */
    public void fillJobRole(String value) {
        selectDropdownOption(jobRoleSelect, value, "Job Role");
    }

    /** id="Department" — options: Academic Affairs, Career Services, Continuing Education, Enrollment Management, Executive Leadership, International, Strategic Planning, Student Affairs, Teaching/Faculty/Research, Other */
    public void fillDepartment(String value) {
        selectDropdownOption(departmentSelect, value, "Department");
    }

    /** id="Self_Reported_Needs__c" — options: Get in touch with sales, Existing customer support, Learner Support, Courses for myself, Other */
    public void fillNeeds(String value) {
        selectDropdownOption(needsSelect, value, "Needs");
    }

    /** id="Country" — standard country SELECT */
    public void fillCountry(String value) {
        selectDropdownOption(countrySelect, value, "Country");
    }

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
