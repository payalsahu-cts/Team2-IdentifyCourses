# 🤖 Hackathon Ideas — Web Automation Project

![Automation](https://img.shields.io/badge/Automation-Selenium%204-brightgreen?style=for-the-badge&logo=selenium)
![Language](https://img.shields.io/badge/Language-Java%2017-ED8B00?style=for-the-badge&logo=openjdk)
![Build](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven)
![Framework](https://img.shields.io/badge/Framework-TestNG-orange?style=for-the-badge)
![Site](https://img.shields.io/badge/Target%20Site-Coursera.org-0056D2?style=for-the-badge&logo=coursera)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)

---

## 📌 Problem Statement

> **Hackathon Ideas — Identify Courses**
> Automate the discovery, extraction, and validation of web content from [Coursera.org](https://www.coursera.org) using **Selenium WebDriver with Java**, covering course search, catalog scraping, and enterprise form validation.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Automation Scenarios](#-automation-scenarios)
- [Key Automation Scope](#-key-automation-scope)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Configuration](#-configuration)
- [Running the Tests](#-running-the-tests)
- [Expected Output](#-expected-output)
- [Sample Results](#-sample-results)

---

## 🌐 Overview

This project uses **Selenium WebDriver 4 + Java + TestNG + Maven** to automate three end-to-end scenarios on Coursera.org. It demonstrates key browser automation skills including multi-window handling, dynamic dropdown extraction, form interaction, scrolling, and error message capture.

| Scenario | Description |
|----------|-------------|
| **1** | Search Web Development courses (Beginner / English) → extract first 2 results |
| **2** | Language Learning catalog → extract all languages, levels & course counts |
| **3** | Enterprise → Campus form → submit invalid email → capture error message |

---

## 🧪 Automation Scenarios

---

### Scenario 1 — 🔍 Web Development Courses for Beginners

**Objective:** Search and display beginner-level Web Development courses offered in English and extract details for the first two results.

**Automation Steps:**
1. Launch browser and navigate to `https://www.coursera.org`
2. Click the **Search** bar and type `"Web Development"`
3. Press **Enter** and wait for search results to load
4. Apply filter → **Level: Beginner**
5. Apply filter → **Language: English**
6. Extract the following for the **first 2 courses**:
   - ✅ Course Name
   - ✅ Total Learning Hours
   - ✅ Rating (out of 5)
7. Print extracted data in tabular format to console
8. Save results to `outputs/scenario1_results.csv`

**Expected Console Output:**
```
╔══════════════════════════════════════════════════════════════════╗
║        SCENARIO 1 — Web Development Courses (Beginners)         ║
╠════╦═════════════════════════════════════╦════════════╦═════════╣
║ #  ║ Course Name                         ║ Hours      ║ Rating  ║
╠════╬═════════════════════════════════════╬════════════╬═════════╣
║ 1  ║ Introduction to Web Development     ║ 17 Hours   ║ ⭐ 4.7 ║
║ 2  ║ HTML, CSS and Javascript for Devs   ║ 21 Hours   ║ ⭐ 4.8 ║
╚════╩═════════════════════════════════════╩════════════╩═════════╝
✅ Results saved → outputs/scenario1_results.csv
```

---

### Scenario 2 — 🌍 Language Learning Catalog Extraction

**Objective:** Navigate to the Language Learning section and extract all available languages, their proficiency levels, and the total course count for each combination.

**Automation Steps:**
1. Navigate to `https://www.coursera.org`
2. Search for or browse to the **"Language Learning"** category
3. Open the **Language** dropdown filter — extract all options into a `List<String>`
4. Open the **Level** dropdown filter — extract all options into a `List<String>`
5. For each language–level combination, record the **total course count**
6. Store all results in a `List<Map<String, String>>` collection
7. Display and save results to `outputs/scenario2_results.csv`

**Expected Console Output:**
```
╔══════════════════════════════════════════════════════╗
║       SCENARIO 2 — Language Learning Catalog         ║
╠═══════════════╦══════════════════╦═══════════════════╣
║ Language      ║ Level            ║ Total Courses     ║
╠═══════════════╬══════════════════╬═══════════════════╣
║ Spanish       ║ Beginner         ║ 12                ║
║ Spanish       ║ Intermediate     ║ 8                 ║
║ Spanish       ║ Advanced         ║ 5                 ║
║ French        ║ Beginner         ║ 9                 ║
║ Mandarin      ║ Mixed            ║ 7                 ║
║ ...           ║ ...              ║ ...               ║
╠═══════════════╩══════════════════╩═══════════════════╣
║ Total Records Extracted: XX                          ║
╚══════════════════════════════════════════════════════╝
✅ Results saved → outputs/scenario2_results.csv
```

---

### Scenario 3 — 🏢 Enterprise Form — Validation Error Capture

**Objective:** Navigate to "For Enterprise", open Coursera for Campus, fill the "Ready to transform" form with an invalid email, and capture the validation error message.

**Automation Steps:**
1. Navigate back to `https://www.coursera.org` (Home Page)
2. Click **"For Enterprise"** in the top navigation bar
3. Under **Product**, click **"Coursera for Campus"** (handles new tab/window switch if opened)
4. Scroll down to the **"Ready to transform your organization?"** form
5. Fill all fields, with email intentionally invalid:
   - Name: `Test User`
   - Organization: `Test Org`
   - Email: `invalidemail@@broken` ← *intentionally invalid*
   - Phone: `9999999999`
6. Click **Submit / Get Started**
7. Capture the **error/warning message** displayed for the invalid email field
8. Print message and save to `outputs/scenario3_error_log.txt`

**Expected Console Output:**
```
╔══════════════════════════════════════════════════════════════╗
║     SCENARIO 3 — Enterprise Form Validation Test             ║
╠══════════════╦══════════════════════════╦════════════════════╣
║ Field        ║ Input Value              ║ Validation Status  ║
╠══════════════╬══════════════════════════╬════════════════════╣
║ Name         ║ Test User                ║ ✅ Valid           ║
║ Organization ║ Test Org                 ║ ✅ Valid           ║
║ Email        ║ invalidemail@@broken     ║ ❌ Invalid         ║
║ Phone        ║ 9999999999               ║ ✅ Valid           ║
╚══════════════╩══════════════════════════╩════════════════════╝
⚠️  Captured Error Message:
    → "Please enter a valid email address."
✅ Error log saved → outputs/scenario3_error_log.txt
```

---

## 🎯 Key Automation Scope

| # | Automation Skill | Java / Selenium API Used | Applied In |
|---|-----------------|--------------------------|------------|
| 1 | **Handling different browser windows/tabs** | `driver.getWindowHandles()`, `driver.switchTo().window()` | Scenario 3 |
| 2 | **Search bar interaction** | `sendKeys()`, `Keys.ENTER` | Scenario 1 |
| 3 | **Extracting dropdown items into Collections** | `Select`, `getOptions()`, `List<WebElement>` | Scenario 2 |
| 4 | **Navigating back to Home Page** | `driver.navigate().to(url)` | Scenario 2 → 3 |
| 5 | **Filling form fields** | `sendKeys()`, `click()`, `clear()` | Scenario 3 |
| 6 | **Capturing validation/warning messages** | `findElement().getText()` | Scenario 3 |
| 7 | **Scrolling down in web page** | `JavascriptExecutor` → `window.scrollBy()` | Scenario 3 |

---

## 🛠 Tech Stack

| Tool / Library | Version | Purpose |
|---------------|---------|---------|
| **Java (JDK)** | 17+ | Core programming language |
| **Selenium WebDriver** | 4.18.x | Browser automation |
| **TestNG** | 7.9.x | Test framework & execution |
| **Maven** | 3.8+ | Build tool & dependency management |
| **WebDriverManager** | 5.7.x | Automatic ChromeDriver/GeckoDriver management |
| **OpenCSV** | 5.9 | CSV output file writing |
| **ExtentReports** | 5.1.x | HTML test report generation |
| **Log4j2** | 2.23.x | Logging framework |

---


## ✅ Prerequisites

- **JDK 17+** installed and `JAVA_HOME` configured
- **Maven 3.8+** installed and available in system `PATH`
- **Google Chrome** (latest stable) or **Mozilla Firefox**
- Active internet connection

> ℹ️ **ChromeDriver / GeckoDriver** is managed automatically by `WebDriverManager` — no manual driver download needed.

---

## 📦 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/payalsahu-cts/Team2-IdentifyCourses.git
cd Team2-IdentifyCourses
```

### 2. Install All Dependencies

```bash
mvn clean install -DskipTests
```

---

## ⚙️ Configuration

Edit `src/main/resources/config.properties` to match your environment:

```properties
baseURL=https://www.coursera.org
browser=chrome
implicitWait=10
explicitWait=20
screenshotPath=screenshots/
```

---

## `pom.xml` — Core Dependencies

```xml
<dependencies>

    <!-- Selenium WebDriver -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.18.1</version>
    </dependency>

    <!-- WebDriverManager — automatic driver binary management -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.7.0</version>
    </dependency>

    <!-- TestNG test framework -->
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.9.0</version>
        <scope>test</scope>
    </dependency>

    <!-- ExtentReports for HTML test reports -->
    <dependency>
        <groupId>com.aventstack</groupId>
        <artifactId>extentreports</artifactId>
        <version>5.1.1</version>
    </dependency>

    <!-- OpenCSV for writing CSV output files -->
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>5.9</version>
    </dependency>

    <!-- Log4j2 logging -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.23.1</version>
    </dependency>

</dependencies>
```

---

## `testng.xml` — Suite Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="IdentifyCourses Test Suite" parallel="tests" thread-count="3">

    <test name="Member3 - Web Dev Courses" verbose="2" preserve-order="true">
        <parameter name="cucumber.filter.tags" value="@webdev"/>
        <classes>
            <class name="IdentifyCourses.runners.CucumberRunner"/>
        </classes>
    </test>

    <test name="Member2 - Language Learning" verbose="2" preserve-order="true">
        <parameter name="cucumber.filter.tags" value="@languagelearning"/>
        <classes>
            <class name="IdentifyCourses.runners.CucumberRunner"/>
        </classes>
    </test>

    <test name="Member5 - Enterprise Campus Form" verbose="2" preserve-order="true">
        <parameter name="cucumber.filter.tags" value="@enterprise"/>
        <classes>
            <class name="IdentifyCourses.runners.CucumberRunner"/>
        </classes>
    </test>

</suite>
```

---

## ▶️ Run All Scenarios (Full Suite)

```bash
mvn test
```


---

## 📊 Expected Output

After a successful run, the following files are generated automatically:

| Output File | Description |
|-------------|-------------|
| `outputs/scenario1_results.csv` | Course name, total hours, rating for first 2 courses |
| `outputs/scenario2_results.csv` | Language, proficiency level, and course count per combination |
| `outputs/scenario3_error_log.txt` | Captured validation error message with timestamp |
| `reports/TestReport.html` | Full ExtentReports HTML test execution report |
| `screenshots/*.png` | Auto-captured on any test failure |

To view the HTML report after a run:

```bash
# macOS/Linux
open reports/TestReport.html

# Windows
start reports\TestReport.html
```

---

## 🧾 Sample Results

### `outputs/scenario1_results.csv`
```
Rank,Course Name,Total Learning Hours,Rating
1,Introduction to Web Development,17 Hours,4.7
2,HTML CSS and Javascript for Web Developers,21 Hours,4.8
```

### `outputs/scenario2_results.csv`
```
Language,Level,Total Courses
Spanish,Beginner,12
Spanish,Intermediate,8
Spanish,Advanced,5
French,Beginner,9
French,Intermediate,6
Mandarin,Beginner,7
Japanese,Mixed,4
```

### `outputs/scenario3_error_log.txt`
```
==============================================
  Scenario 3 — Enterprise Form Error Capture
==============================================
Timestamp    : 2026-05-07 10:34:21
URL          : https://www.coursera.org/campus
Field        : Email
Input Value  : invalidemail@@broken
Error Message: "Please enter a valid email address."
Test Status  : PASSED ✅
==============================================
```

---


## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Built with ☕ **Java** + 🤖 **Selenium WebDriver 4** + 🧪 **TestNG**  
for the **Hackath Ideas** Web Automation Challenge

</div>
