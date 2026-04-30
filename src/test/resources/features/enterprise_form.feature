@enterprise
Feature: Coursera Enterprise Campus Form Interaction

  Background:
    Given I am on the Coursera homepage

  @invalid_email
  Scenario: Navigate to Courses for Campus, fill form with invalid email and capture validation error
    When I click "For Enterprise" in the header navigation
    And I hover over the "Products" menu
    And I click "Courses for Campus"
    And I scroll to the "Ready to transform" form section
    And I fill the form with the following valid details:
      | field           | value                   |
      | firstName       | Test                    |
      | lastName        | Automation              |
      | email           | testuser@university.edu |
      | phone           | 9876543210              |
      | institutionType | University              |
      | institutionName | State University        |
      | jobRole         | IT Administrator        |
      | department      | Information Technology  |
      | needs           | Campus                  |
      | country         | India                   |
    And I replace the email field with invalid email "testinvalid.com"
    And I click the Submit button
    Then a validation error message is displayed for the email field
    And the error message text is captured and printed to the console
    And a screenshot of the error state is saved
