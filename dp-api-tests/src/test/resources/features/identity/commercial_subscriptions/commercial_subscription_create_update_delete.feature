@Identity
Feature: Commercial subscription create update delete

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | Id                                   | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 2a2f76f3-3537-4d5a-971a-7a36f61095bd | partner | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "2a2f76f3-3537-4d5a-971a-7a36f61095bd"
      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |


  # --------------------- CREATE ---------------------
  Scenario Outline: Checking error codes for creating commercial subscription
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                                                 | method | module   | url                                | error_code | custom_code |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_application_id.json | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_customer_id.json    | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_property_id.json    | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |


  # --------------------- DELETE ---------------------

  Scenario: Checking error code for deleting commercial subscription
    When Nonexistent commercial subscription id is deleted
    Then Response code is "404"
    And Custom code is "40402"


  # --------------------- UPDATE ---------------------
  Scenario: Update commercial subscription - inactivate
    Given Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is activated
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deactivated
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is not activate


  Scenario Outline: Send POST request with empty body to all commercial subcriptions endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                    |
      | identity/commercial_subscriptions/                                     |
      | identity/commercial_subscriptions/8e238f8e-2c9c-4e32-9a63-40474a9728eb |
