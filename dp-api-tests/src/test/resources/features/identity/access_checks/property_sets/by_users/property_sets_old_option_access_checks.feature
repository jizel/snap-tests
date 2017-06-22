@Identity
Feature: Property sets old option access check feature - DP-1576
  - User has (implicit) access to a PropertySet if he has access to all Properties that at the moment belong to this PropertySet.
      - this applies only to the endpoint identity/customers/{customer_id}/property_sets
      - this implicitly granted access is not propagated to child property sets (in contrast with the explicitly granted access) - need to test

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | type     | username                | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | userWithImplicitPropSet | Customer1 | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithExplicitPropSet | Customer2 | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following properties exist with random address and billing address for user "userWithImplicitPropSet"
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_1 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithExplicitPropSet"
      | name            | type            | id                                   |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists with is_active "false"
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists with is_active "false"
    Given Relation between property with code "p3_code" and property set with name "ps1_name" exists with is_active "false"

    Scenario: User with all properties has implicit access to customers properties
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "0"
      When Relation between property "p1_code" and property set "ps1_name" is activated
      When Relation between property "p2_code" and property set "ps1_name" is activated
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "0"
      When Relation between property "p3_code" and property set "ps1_name" is activated
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      And Total count is "1"
      When Property set "ps1_name" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Body contains property with attribute "name" value "ps1_name"

    Scenario: When a single property is added to property set, user loses implicit access, when deleted he gains access again
      When Relation between property "p1_code" and property set "ps1_name" is activated
      When Relation between property "p2_code" and property set "ps1_name" is activated
      When Relation between property "p3_code" and property set "ps1_name" is activated
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Total count is "1"
      Given The following properties exist with random address and billing address for user "userWithExplicitPropSet"
        | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
        | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Given Relation between property "p4_code" and property set "ps1_name" exists
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "0"
      When Relation between property "p4_code" and property set "ps1_name" is deleted
      Then Response code is "204"
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "1"

    Scenario: Single property is deleted from property set, user loses implicit access, when added he gains access again
      Given Relation between property "p1_code" and property set "ps1_name" is activated
      Given Relation between property "p2_code" and property set "ps1_name" is activated
      Given Relation between property "p3_code" and property set "ps1_name" is activated
      When User "userWithImplicitPropSet" is removed from property with code "p1_code"
      Then Response code is "204"
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "0"
      When User "userWithImplicitPropSet" is added to property with code "p1_code" with is_active "false"
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "0"
      Given Relation between user "userWithImplicitPropSet" and property "p1_code" is activated
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "1"

    Scenario: Implicit access is not propagated to child property sets
      Given Relation between property "p1_code" and property set "ps1_name" is activated
      Given Relation between property "p2_code" and property set "ps1_name" is activated
      Given Relation between property "p3_code" and property set "ps1_name" is activated
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithExplicitPropSet"
        | name            | type            | parentId                             | id                                   |
        | childPS1        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithExplicitPropSet"
      Then Response code is "200"
      And Total count is "2"
      When List of all property sets for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithImplicitPropSet"
      Then Response code is "200"
      And Total count is "1"
      When Property set "childPS1" is requested by user "userWithImplicitPropSet"
      Then Response code is "404"