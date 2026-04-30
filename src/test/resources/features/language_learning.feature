@languagelearning
Feature: Language Learning Courses - Filter Analysis on Coursera
  As a learner
  I want to explore Language Learning courses on Coursera
  And extract all available language options and difficulty levels with their counts
  So that I can understand the full distribution of courses across languages and levels

  @scenario2
  Scenario: Extract all language and level filter options from Language Learning search
    Given I open the Coursera website for language learning research
    When I search for "Language Learning" courses on Coursera
    Then Language Learning search results should be displayed on the page
    When I extract all available language filter options with their course counts
    And I extract all available difficulty level filter options with their course counts
    Then the language and level distribution data is printed to the console
    And the language and level data is exported to Excel file "target/testdata/language_learning.xlsx"
