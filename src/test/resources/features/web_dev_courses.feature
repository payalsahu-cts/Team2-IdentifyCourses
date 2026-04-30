@webdev
Feature: Web Development Courses Search on Coursera
  As a learner
  I want to search for web development courses on Coursera
  And filter results to Beginner level and English language
  So that I can identify and record the top beginner-friendly English web development courses

  Background:
    Given I am on the Coursera homepage

  @scenario1
  Scenario: Search web development, apply Beginner + English filters, extract top 2 results
    When I search for "web development"
    And I apply the "Beginner" level filter
    And I apply the "English" language filter
    Then I should see course results on the page
    When I extract details from the first 2 course results
    Then the extracted course details are printed to the console
    And the course data is written to Excel file "target/testdata/web_dev_courses.xlsx"
