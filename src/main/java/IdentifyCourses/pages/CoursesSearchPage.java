package IdentifyCourses.pages;

import IdentifyCourses.base.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoursesSearchPage {

    // Search input on homepage
    private final By searchInput = By.xpath(
            "//input[@name='query' or @data-testid='search-field' "
            + "or contains(@placeholder,'What do you want to learn')]"
    );

    // Confirm results loaded — presence of any h3 on the page
    private final By resultsContainer = By.xpath("(//h3)[1]");

    // Course cards in search results — li or div containing an h3 AND a course link
    private final By courseCards = By.xpath(
            "//li[.//h3 and .//a[contains(@href,'/learn/') "
            + "or contains(@href,'/specializations/') "
            + "or contains(@href,'/professional-certificates/')]]"
    );

    // Link inside a card
    private final By cardLink = By.xpath(
            ".//a[contains(@href,'/learn/') or contains(@href,'/specializations/') "
            + "or contains(@href,'/professional-certificates/')]"
    );

    // Title inside a card
    private final By cardTitle = By.xpath(".//h3");

    // Rating + metadata block inside a card (we'll parse the number out via regex)
    private final By cardMeta = By.xpath(
            ".//*[contains(@class,'ratings') or contains(@class,'rating') "
            + "or contains(@class,'star') or contains(@class,'ReviewCount')]"
            + " | .//span[contains(.,'·')]"
    );

    // Duration on the card — e.g. "1 - 4 Weeks", "3 - 6 Months"
    private final By cardDuration = By.xpath(
            ".//*[contains(text(),'Week') or contains(text(),'Weeks') "
            + "or contains(text(),'Month') or contains(text(),'Months') "
            + "or contains(text(),'hour') or contains(text(),'Hour')]"
    );

    // Course detail page
    private final By detailTitle = By.xpath("(//h1)[1]");
    private final By detailHours = By.xpath(
            "(//*[contains(text(),'hours') or contains(text(),'Hours') "
            + "or contains(text(),'Weeks') or contains(text(),'Months')]"
            + "[not(self::script)][not(self::style)]"
            + "[not(ancestor::*[contains(@class,'nav') or contains(@class,'footer')])])[1]"
    );
    private final By detailRating = By.xpath(
            "(//*[contains(@class,'ratings') or contains(@class,'CourseRating')]"
            + "//span[string-length(normalize-space()) <= 4 and contains(.,'.')])[1]"
    );

    // ── helpers ───────────────────────────────────────────────────────────────

    private WebDriver driver() { return DriverFactory.getDriver(); }

    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(driver(), Duration.ofSeconds(seconds));
    }

    private void jsClick(WebElement el) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js.executeScript("arguments[0].click();", el);
    }

    private void dismissOverlayIfPresent() {
        try {
            WebElement overlay = driver().findElement(
                    By.xpath("//button[contains(.,'Accept') or contains(.,'Got it') "
                             + "or contains(.,'Continue') or contains(.,'Dismiss')]"));
            overlay.click();
            Thread.sleep(500);
        } catch (NoSuchElementException | InterruptedException ignored) { }
    }

    // Pull a decimal rating like "4.4" out of any text
    private String parseRating(String raw) {
        if (raw == null || raw.isEmpty()) return "N/A";
        Matcher m = Pattern.compile("(\\d\\.\\d)").matcher(raw);
        return m.find() ? m.group(1) : "N/A";
    }

    // Extract just the duration part e.g. "1 - 4 Weeks" or "3 - 6 Months" from full metadata text
    private String extractDuration(String raw) {
        if (raw == null || raw.isEmpty()) return "N/A";
        Matcher m = Pattern.compile(
                "(\\d+\\s*[-–]\\s*\\d+\\s*(?:Week|Weeks|Month|Months|Hour|Hours)"
                + "|\\d+\\s*(?:Week|Weeks|Month|Months|Hour|Hours))"
        ).matcher(raw);
        return m.find() ? m.group(1).trim() : "N/A";
    }

    // ── public API ────────────────────────────────────────────────────────────

    public void searchFor(String keyword) {
        dismissOverlayIfPresent();
        WebElement input = wait(20).until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(keyword + Keys.ENTER);
        wait(20).until(ExpectedConditions.urlContains("/search"));
        wait(20).until(ExpectedConditions.presenceOfElementLocated(resultsContainer));
        System.out.println("[Page] Search results loaded. URL: " + driver().getCurrentUrl());
    }

    /**
     * Clicks the Level filter chip (e.g. "Level", "Difficulty") to open the dropdown,
     * then selects the given level option (e.g. "Beginner") from the opened dropdown.
     * Falls back to URL parameter if the dropdown approach fails.
     */
    public void applyLevelFilter(String level) {
        // Scroll to top so filter chips are visible
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, 0)");
        try { Thread.sleep(500); } catch (InterruptedException ignored) { }

        try {
            // Step 1: click the Level chip to open its dropdown
            WebElement chip = findFilterChip("Level", "Difficulty", "Skill Level");
            jsClick(chip);
            System.out.println("[Filter] Opened Level dropdown");
            Thread.sleep(800);

            // Step 2: click the option inside the dropdown
            selectDropdownOption(level);
            Thread.sleep(1500);
            System.out.println("[Filter] Selected level: " + level);
        } catch (Exception e) {
            System.out.println("[Filter] Level dropdown failed (" + e.getMessage() + "), using URL.");
            applyFilterViaUrl("productDifficultyLevel", level);
        }
    }

    /**
     * Clicks the Language filter chip to open the dropdown,
     * then selects the given language (e.g. "English").
     * Falls back to URL parameter if the dropdown approach fails.
     */
    public void applyLanguageFilter(String language) {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, 0)");
        try { Thread.sleep(500); } catch (InterruptedException ignored) { }

        try {
            // Step 1: click the Language chip
            WebElement chip = findFilterChip("Language");
            jsClick(chip);
            System.out.println("[Filter] Opened Language dropdown");
            Thread.sleep(800);

            // Step 2: click the option
            selectDropdownOption(language);
            Thread.sleep(1500);
            System.out.println("[Filter] Selected language: " + language);
        } catch (Exception e) {
            System.out.println("[Filter] Language dropdown failed (" + e.getMessage() + "), using URL.");
            applyFilterViaUrl("language", language);
        }
    }

    /**
     * Finds a filter chip button in the top filter bar whose label contains
     * any of the given keywords (case-insensitive match via contains).
     */
    private WebElement findFilterChip(String... keywords) {
        StringBuilder xb = new StringBuilder();
        xb.append("//button[");
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) xb.append(" or ");
            xb.append("contains(.,'").append(keywords[i]).append("')");
        }
        xb.append("] | //div[@role='button'][");
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) xb.append(" or ");
            xb.append("contains(.,'").append(keywords[i]).append("')");
        }
        xb.append("]");
        return new WebDriverWait(driver(), Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(By.xpath(xb.toString())));
    }

    /**
     * Selects an option from an open dropdown by matching its visible text exactly.
     * Covers role=option, role=menuitem, label, li, and class-based option containers.
     */
    private void selectDropdownOption(String optionText) {
        By loc = By.xpath(
                "//*[@role='option' or @role='menuitem'][normalize-space()='" + optionText + "'] | "
                + "//label[normalize-space()='" + optionText + "' "
                + "  or .//span[normalize-space()='" + optionText + "']] | "
                + "//li[normalize-space()='" + optionText + "'] | "
                + "//div[contains(@class,'option') or contains(@class,'item') or contains(@class,'Option')]"
                + "[normalize-space()='" + optionText + "']"
        );
        WebElement option = new WebDriverWait(driver(), Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(loc));
        jsClick(option);
    }

    private void applyFilterViaUrl(String paramName, String paramValue) {
        String currentUrl = driver().getCurrentUrl();
        String separator  = currentUrl.contains("?") ? "&" : "?";
        driver().get(currentUrl + separator + paramName + "=" + paramValue);
        wait(20).until(ExpectedConditions.presenceOfElementLocated(resultsContainer));
        System.out.println("[Filter] Applied via URL: " + paramName + "=" + paramValue);
    }

    public boolean areResultsDisplayed() {
        try {
            return !wait(15).until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(courseCards)).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Extracts course details from the first {@code count} result cards.
     *
     * Strategy:
     *  1. Scroll to top and collect card names, links, ratings, durations all at once
     *     (before any navigation, so no StaleElementReferenceException).
     *  2. Navigate to each course URL directly, handle new-tab if one opens.
     *  3. Read hours and detailed rating from the course detail page.
     */
    public List<String[]> extractCourseDetails(int count) {
        List<String[]> results = new ArrayList<>();
        JavascriptExecutor js = (JavascriptExecutor) driver();

        // Scroll to top — filters were applied via URL so no AI overview remains
        js.executeScript("window.scrollTo(0, 0)");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) { }

        // Collect all cards, sorted top-to-bottom
        List<WebElement> allCards = wait(15)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(courseCards));
        allCards = allCards.stream()
                .sorted(Comparator.comparingInt(el -> el.getLocation().getY()))
                .collect(Collectors.toList());

        System.out.println("[Page] Total search-result cards found: " + allCards.size());

        int limit = Math.min(count, allCards.size());

        // ── Step 1: Read all card data upfront (name, rating, duration, href) ──
        List<String[]> cardData = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            WebElement card = allCards.get(i);

            // Get entire card text and extract rating using regex (e.g. "4.4")
            String allCardText = card.getText();
            String name        = safeText(card, cardTitle);
            String rating      = parseRating(allCardText);

            // Extract clean duration like "1 - 4 Weeks" or "3 - 6 Months" from card text
            String duration    = extractDuration(allCardText);

            String href = "";
            try {
                href = card.findElement(cardLink).getAttribute("href");
            } catch (NoSuchElementException ignored) { }

            cardData.add(new String[]{name, rating, duration, href});
            System.out.printf("[Card %d] Name: %s | Rating: %s | Duration: %s | URL: %s%n",
                    i + 1, name, rating, duration, href);
        }

        // ── Step 2: Visit each course page to get hours, handle new tab ──
        String originalWindow = driver().getWindowHandle();

        for (int i = 0; i < cardData.size(); i++) {
            String[] cd    = cardData.get(i);
            String name    = cd[0];
            String rating  = cd[1];
            String hours   = cd[2].isEmpty() ? "N/A" : cd[2]; // card duration as fallback
            String href    = cd[3];

            if (!href.isEmpty()) {
                Set<String> beforeHandles = driver().getWindowHandles();

                // Navigate directly to course URL
                driver().get(href);

                // Handle case where a new tab was opened instead
                String newWindow = waitForNewWindow(beforeHandles, 3);
                boolean isNewTab = newWindow != null;
                if (isNewTab) {
                    driver().switchTo().window(newWindow);
                    System.out.println("[Tab] Switched to new tab for course " + (i + 1));
                }

                try {
                    wait(15).until(ExpectedConditions.presenceOfElementLocated(detailTitle));
                } catch (TimeoutException ignored) { }

                // Prefer the detail page h1 as the course name
                String detailName = safeTextBy(detailTitle);
                if (!detailName.isEmpty()) name = detailName;

                // Try to get hours from detail page; keep card duration if not found
                String detailHoursText = extractHoursText();
                if (!detailHoursText.equals("N/A")) hours = detailHoursText;

                // Try to get rating from detail page if card didn't have it
                if (rating.equals("N/A")) {
                    String detailRatingText = safeTextBy(detailRating);
                    rating = parseRating(detailRatingText);
                }

                // Close new tab and return to results, or navigate back
                if (isNewTab) {
                    driver().close();
                    driver().switchTo().window(originalWindow);
                } else {
                    driver().navigate().back();
                    wait(10).until(ExpectedConditions.urlContains("/search"));
                }
            }

            System.out.printf("[Course %d] Name: %s | Hours: %s | Rating: %s%n",
                    i + 1, name, hours, rating);
            results.add(new String[]{name, hours, rating});
        }

        return results;
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private String safeText(WebElement parent, By by) {
        try { return parent.findElement(by).getText().trim(); }
        catch (NoSuchElementException e) { return ""; }
    }

    private String safeTextBy(By by) {
        try { return driver().findElement(by).getText().trim(); }
        catch (NoSuchElementException e) { return ""; }
    }

    private String extractHoursText() {
        try {
            List<WebElement> els = driver().findElements(detailHours);
            return els.stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .min(Comparator.comparingInt(String::length))
                    .orElse("N/A");
        } catch (Exception ignored) { }
        return "N/A";
    }

    private String waitForNewWindow(Set<String> before, int timeoutSec) {
        long deadline = System.currentTimeMillis() + (timeoutSec * 1000L);
        while (System.currentTimeMillis() < deadline) {
            Set<String> current = driver().getWindowHandles();
            if (current.size() > before.size()) {
                for (String h : current) {
                    if (!before.contains(h)) return h;
                }
            }
            try { Thread.sleep(300); } catch (InterruptedException ignored) { }
        }
        return null;
    }
}
