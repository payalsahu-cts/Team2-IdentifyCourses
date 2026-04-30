package IdentifyCourses.pages;

import IdentifyCourses.base.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageLearningPage {

    private static final By SEARCH_INPUT = By.xpath(
            "//input[@name='query' or @data-testid='search-field'"
            + " or contains(@placeholder,'What do you want to learn')]"
    );
    private static final By RESULTS_INDICATOR = By.xpath("(//h3)[1]");

    // ── driver helpers ────────────────────────────────────────────────────────

    private WebDriver driver() { return DriverFactory.getDriver(); }

    private WebDriverWait wait(int seconds) {
        return new WebDriverWait(driver(), Duration.ofSeconds(seconds));
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private void jsClick(WebElement el) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        js.executeScript("arguments[0].click();", el);
    }

    // ── public API ────────────────────────────────────────────────────────────

    public void searchForLanguageLearning() {
        dismissOverlayIfPresent();
        WebElement input = wait(20).until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        input.clear();
        input.sendKeys("Language Learning" + Keys.ENTER);
        wait(20).until(ExpectedConditions.urlContains("/search"));
        wait(20).until(ExpectedConditions.presenceOfElementLocated(RESULTS_INDICATOR));
        System.out.println("[Page] Language Learning results loaded: " + driver().getCurrentUrl());
    }

    public boolean areResultsDisplayed() {
        try {
            return !wait(15).until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(RESULTS_INDICATOR)
            ).isEmpty();
        } catch (TimeoutException e) { return false; }
    }

    /** Click the Language filter chip and return all language→count pairs. */
    public LinkedHashMap<String, Integer> extractLanguageOptions() {
        System.out.println("[Page] Extracting Language filter options...");
        LinkedHashMap<String, Integer> opts = extractAllFilterOptions("Language");
        System.out.println("[Page] Languages extracted: " + opts.size());
        return opts;
    }

    /** Read level options from the sidebar (no chip click needed) with chip fallback. */
    public LinkedHashMap<String, Integer> extractLevelOptions() {
        System.out.println("[Page] Extracting Level filter options...");

        // Coursera keeps Beginner/Intermediate/Advanced/Mixed in a static sidebar section.
        // Try reading it directly before falling back to the chip-click approach.
        LinkedHashMap<String, Integer> opts = readSidebarSection(
                "Level", "Difficulty Level", "Difficulty", "Skill Level");

        if (opts.isEmpty()) {
            System.out.println("[Page] Sidebar section not found, trying keyword scan...");
            opts = readLevelsByKeyword();
        }

        if (opts.isEmpty()) {
            System.out.println("[Page] Keyword scan empty, trying chip approach...");
            opts = extractAllFilterOptions("Level", "Difficulty", "Skill Level");
        }

        System.out.println("[Page] Levels extracted: " + opts.size());
        return opts;
    }

    /**
     * Finds a filter section by its heading text, expands it if collapsed,
     * and returns all checkbox labels within it.
     */
    private LinkedHashMap<String, Integer> readSidebarSection(String... headings) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, 0)");
        sleep(500);

        for (String heading : headings) {
            try {
                // Find the closest ancestor of the heading that contains checkboxes
                WebElement container = driver().findElement(By.xpath(
                        "//*[normalize-space(text())='" + heading + "']"
                        + "/ancestor::*[count(.//input[@type='checkbox']) >= 1][1]"
                ));

                // Expand the section if it is currently collapsed
                try {
                    WebElement toggle = container.findElement(
                            By.xpath("descendant-or-self::*[@aria-expanded='false']"));
                    jsClick(toggle);
                    sleep(600);
                } catch (org.openqa.selenium.NoSuchElementException ignored) {}

                List<WebElement> labels = container.findElements(
                        By.xpath(".//label[.//input[@type='checkbox']]"));

                if (!labels.isEmpty()) {
                    System.out.println("[Sidebar] '" + heading + "' section → " + labels.size() + " item(s)");
                    for (WebElement l : labels) addParsedOption(l, result);
                    if (!result.isEmpty()) return result;
                }
            } catch (Exception ignored) {}
        }
        return result;
    }

    /**
     * Last-resort level scan: finds any visible checkbox label whose text contains
     * one of the four known Coursera difficulty values.
     */
    private LinkedHashMap<String, Integer> readLevelsByKeyword() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        List<WebElement> labels = driver().findElements(By.xpath(
                "//label[.//input[@type='checkbox']]"
                + "[contains(.,'Beginner') or contains(.,'Intermediate')"
                + " or contains(.,'Advanced') or contains(.,'Mixed')]"
        ));
        System.out.println("[Sidebar] Level keyword scan → " + labels.size() + " label(s)");
        for (WebElement l : labels) addParsedOption(l, result);
        return result;
    }

    // ── filter extraction core ────────────────────────────────────────────────

    /**
     * Opens the filter chip, expands "Show all" if present,
     * reads every option name+count, then closes the dropdown.
     */
    private LinkedHashMap<String, Integer> extractAllFilterOptions(String... chipLabels) {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("window.scrollTo(0, 0)");
        sleep(600);

        try {
            // Snapshot the current state so we can detect the popup that appears
            List<WebElement> preDialogs = driver().findElements(By.xpath("//*[@role='dialog']"));
            int dialogsBefore = preDialogs.size();

            List<WebElement> preCbLabels = driver().findElements(
                    By.xpath("//label[.//input[@type='checkbox']]")
            );

            // Click the filter chip
            WebElement chip = findChip(chipLabels);
            jsClick(chip);
            System.out.println("[Filter] Opened chip: " + chipLabels[0]);
            sleep(1200);

            // Expand "Show all" / "Show more" if available
            tryClickShowAll();

            // Find the option items that appeared
            List<WebElement> items = findNewFilterItems(dialogsBefore, preCbLabels);
            System.out.println("[Filter] " + chipLabels[0] + " → " + items.size() + " item(s)");

            for (WebElement item : items) {
                addParsedOption(item, result);
            }

        } catch (Exception e) {
            System.out.println("[Filter] Failed for '" + chipLabels[0] + "': " + e.getMessage());
        } finally {
            try { driver().findElement(By.tagName("body")).sendKeys(Keys.ESCAPE); }
            catch (Exception ignored) {}
            sleep(600);
        }

        return result;
    }

    /**
     * Builds a flexible XPath to locate a filter chip button by any of the supplied labels.
     */
    private WebElement findChip(String... labels) {
        StringBuilder xb = new StringBuilder("//button[");
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) xb.append(" or ");
            xb.append("contains(.,'").append(labels[i]).append("')");
        }
        xb.append("] | //div[@role='button'][");
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) xb.append(" or ");
            xb.append("contains(.,'").append(labels[i]).append("')");
        }
        xb.append("]");
        return wait(10).until(ExpectedConditions.elementToBeClickable(By.xpath(xb.toString())));
    }

    private void tryClickShowAll() {
        try {
            WebElement btn = new WebDriverWait(driver(), Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath(
                            "//button[contains(.,'Show all') or contains(.,'See all')"
                            + " or contains(.,'Show more') or contains(.,'View all')]"
                    )));
            jsClick(btn);
            System.out.println("[Filter] Clicked 'Show all/more'");
            sleep(800);
        } catch (TimeoutException ignored) {
            System.out.println("[Filter] No 'Show all/more' button present");
        }
    }

    /**
     * Finds filter option elements using a three-tier fallback strategy.
     *
     * Tier 1 — new role="dialog" that appeared after clicking the chip.
     * Tier 2 — new checkbox labels that appeared after clicking (delta approach).
     * Tier 3 — well-known Coursera-specific and generic XPath patterns.
     */
    private List<WebElement> findNewFilterItems(int dialogCountBefore,
                                                List<WebElement> cbLabelsBefore) {
        // ── Tier 1: new dialog/popup ───────────────────────────────────────────
        List<WebElement> currentDialogs = driver().findElements(By.xpath("//*[@role='dialog']"));
        if (currentDialogs.size() > dialogCountBefore) {
            WebElement popup = currentDialogs.get(currentDialogs.size() - 1);
            List<WebElement> labels = popup.findElements(
                    By.xpath(".//label[.//input[@type='checkbox']] | .//*[@role='option']")
            );
            if (!labels.isEmpty()) {
                System.out.println("[Filter] Tier-1 (new dialog): " + labels.size());
                return labels;
            }
        }

        // ── Tier 2: newly appeared checkbox labels (delta) ────────────────────
        List<WebElement> allCbLabels = driver().findElements(
                By.xpath("//label[.//input[@type='checkbox']]")
        );
        if (allCbLabels.size() > cbLabelsBefore.size()) {
            Set<String> before = cbLabelsBefore.stream()
                    .map(e -> {
                        try { return e.getAttribute("innerHTML"); }
                        catch (Exception ex) { return ""; }
                    })
                    .collect(Collectors.toSet());

            List<WebElement> newLabels = allCbLabels.stream()
                    .filter(e -> {
                        try { return !before.contains(e.getAttribute("innerHTML")); }
                        catch (Exception ex) { return true; }
                    })
                    .collect(Collectors.toList());

            if (!newLabels.isEmpty()) {
                System.out.println("[Filter] Tier-2 (delta labels): " + newLabels.size());
                return newLabels;
            }
        }

        // ── Tier 3: known Coursera selector patterns ───────────────────────────
        String[][] patterns = {
            {"//div[@data-testid='filter-options-list']//label", "data-testid list"},
            {"//*[contains(@class,'FilterMenuPopover') or contains(@class,'FilterPopover')"
             + " or contains(@class,'filter-popover')]//label[.//input[@type='checkbox']]",
             "FilterPopover class"},
            {"//*[contains(@class,'FilterMenu') or contains(@class,'filter-menu')]"
             + "//label[.//input[@type='checkbox']]", "FilterMenu class"},
            {"//*[@role='listbox']//*[@role='option']", "listbox→option"},
            {"//*[@data-testid='facet-option']", "facet-option testid"},
            {"//ul[count(.//input[@type='checkbox']) > 1]//li[.//input[@type='checkbox']]//label",
             "ul with many checkboxes"}
        };

        for (String[] p : patterns) {
            try {
                List<WebElement> found = driver().findElements(By.xpath(p[0]));
                if (!found.isEmpty()) {
                    System.out.println("[Filter] Tier-3 (" + p[1] + "): " + found.size());
                    return found;
                }
            } catch (Exception ignored) {}
        }

        // Broadest possible fallback
        System.out.println("[Filter] Using broadest fallback (all checkbox labels)");
        return driver().findElements(By.xpath("//label[.//input[@type='checkbox']]"));
    }

    /**
     * Parses one option element into a name+count entry and adds it to the map.
     *
     * Handles three common formats:
     *   Two child spans: <span>English</span><span>1,234</span>
     *   Two-line text:   "English\n1,234"
     *   Single-line:     "English (1,234)" or "English 1234"
     */
    private void addParsedOption(WebElement el, LinkedHashMap<String, Integer> map) {
        try {
            // ── Try child span structure first ─────────────────────────────────
            List<WebElement> spans = el.findElements(
                    By.xpath(".//span[string-length(normalize-space()) > 0"
                             + " and not(.//span)]")
            );
            if (spans.size() >= 2) {
                String name     = spans.get(0).getText().trim();
                String lastSpan = spans.get(spans.size() - 1).getText().trim();
                String digits   = lastSpan.replaceAll("[^\\d]", "");
                if (!name.isEmpty() && !digits.isEmpty()
                        && name.matches("[a-zA-Z][\\w\\s\\-()/]*")
                        && !name.equalsIgnoreCase(lastSpan)) {
                    map.put(name, Integer.parseInt(digits));
                    return;
                }
            }

            // ── Fall back to full element text ─────────────────────────────────
            String text = el.getText().trim().replaceAll("[✓✔☑\\[\\]]", "").trim();
            if (text.isEmpty()) return;

            // Two-line: "Name\nCount"
            String[] lines = text.split("\n");
            if (lines.length >= 2) {
                String name     = lines[0].trim();
                String countRaw = lines[lines.length - 1].trim().replaceAll("[^\\d]", "");
                if (!name.isEmpty() && !countRaw.isEmpty()
                        && name.matches("[a-zA-Z][\\w\\s\\-()/]*")) {
                    map.put(name, Integer.parseInt(countRaw));
                    return;
                }
            }

            // Single-line: "Name (count)" or "Name count"
            Matcher m = Pattern.compile(
                    "^([a-zA-Z][\\w\\s\\-()/]+?)\\s*\\(?([\\d,]+)\\)?\\s*$"
            ).matcher(text);
            if (m.matches()) {
                String name = m.group(1).trim();
                int count   = Integer.parseInt(m.group(2).replace(",", ""));
                if (!name.isEmpty()) {
                    map.put(name, count);
                }
            }

        } catch (StaleElementReferenceException ignored) {
        } catch (NumberFormatException e) {
            System.out.println("[Filter] Skipping (count parse error): " + safeText(el));
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void dismissOverlayIfPresent() {
        try {
            WebElement el = driver().findElement(By.xpath(
                    "//button[contains(.,'Accept') or contains(.,'Got it')"
                    + " or contains(.,'Continue') or contains(.,'Dismiss')]"
            ));
            el.click();
            sleep(500);
        } catch (org.openqa.selenium.NoSuchElementException | StaleElementReferenceException ignored) {}
    }

    private String safeText(WebElement el) {
        try { return el.getText().trim(); }
        catch (Exception e) { return "<unavailable>"; }
    }
}
