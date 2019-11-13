Feature: Check if phone is available

  Scenario: Checking phone number by REST-protocol
    Given I use REST-service on address "localhost:8080" with version "1"
    And I want to "checkPhoneNumber"
    When I sending "phoneNumber" with value "+79991234567"
    Then I receive in JSON body