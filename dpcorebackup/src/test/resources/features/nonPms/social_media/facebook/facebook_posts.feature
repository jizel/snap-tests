@SocialMedia
Feature: facebook posts
  Testing of api for facebook posts with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Getting a list of items
    When List of facebook items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is 200
    And Content type is "application/json"
    And There are <count> posts returned

    Examples:
      | url                       | limit | cursor | count | property                             |
      | /analytics/facebook/posts |       |        | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | /null |        | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts |       | /null  | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 51    |        | 51    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 51    | /null  | 51    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts |       | 1      | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | /null | 1      | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 20    | 0      | 20    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 60    | 0      | 60    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 5     | 5      | 5     | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Checking error codes for getting list of items
    When List of facebook items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is 400
    And Custom code is "40002"

    Examples:
      | url                       | limit | cursor | property                             |
      | /analytics/facebook/posts | /null | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts |       | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 10    | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  | 0      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  | /null  | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  |        | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 10    | text   | 99000099-9999-4999-a999-999999999999 |


  Scenario Outline: Missing property ID
    When List of facebook items "<url>" for property id "/null" is got with limit "/null" and cursor "/null"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples:
      | url                       | response_code | custom_code |
      | /analytics/facebook/posts | 400           | 40002       |


  @Smoke
  Scenario Outline: Checking that data returned are correct (date(UTC) from DB is recalculated to property timezone)
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | type     | username      | firstName | lastName | email                        | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotuser1@snapshot.travel | Europe/Prague | cs-CZ   |
    And The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone     | customerId                           |
      | 99001099-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Asia/Bangkok | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    And List of facebook items "<url>" for property id "<property>" is got with limit "1" and cursor "/null"
    Then Response code is 200
    And Facebook posts contains datetime "<datetime>" engagement "<engagement>" content "<content>" and reach "<reach>"
#    This test expects the sql data from load.sql script to be loaded on the same day when it is run
    Examples:
      | url                       | datetime      | engagement | content       | reach | property                             |
      | /analytics/facebook/posts | 2017-07-17    | 4168       | Post 10 - 500 | 11553 | 99001099-9999-4999-a999-999999999999 |