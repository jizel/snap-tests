@Identity
Feature: Customers-Property Sets Application access check feature - GET
  - Checking when certain application should and should not have access to certain customers' property sets
  - Only Customers for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - Only Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | CustomerWithoutSubscription | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithCust1"
      | Id                                   | name            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | prop_set1       | brand           |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd1 | prop_set2       | brand           |
    Given Relation between user "user1" and default property exists
    Given Relation between user "user2" and default property exists


  Scenario: Second level entities - User sees only property sets he should for customer he sees
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "2"
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "404"

 @skipped
 Scenario: List users by app
    When GET request is sent to "/identity/users" on module "identity" by user "user1" for application version "versionWithSubscription"
    Then There are "2" users returned
    When GET request is sent to "/identity/users" on module "identity" by user "user3" for application version "versionWithSubscription"
    # DP-1889
    Then There are "0" users returned
    When GET request is sent to "/identity/users" on module "identity" by user "user1" for application version "versionWithoutSubscription"
    Then There are "0" users returned
    Given Relation between user "user3" and default property exists
    When GET request is sent to "/identity/users" on module "identity" by user "user3" for application version "versionWithSubscription"
    Then There are "3" users returned


  Scenario: List users of property by app
    When User "user1" requests list of users for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then There are "2" users returned
    When User "user1" requests list of users for property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "404"

  # DP-1893
  # DP-1892
  @skipped
  Scenario: List users of customer by app
    # User with explicit access to the property will try to access the user with access to this property
    # through property set using application with commercial subscription to only one property in this property set
    Given The following property is created with random address and billing address
      | salesforceId   | name    | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    And Relation between user "user3" and property with code "p1_code" exists
    Given The following property set is created for customer with id "12300000-0000-4000-a000-000000000000"
      | propertySetName | propertySetDescription | propertySetType |
      | myPs            | someDesc               | brand           |
    And Property "defaultPropertyCode" is added to property set "myPs"
    And Property "p1_code" is added to property set "myPs"
    And Relation between user "user3" and property set "myPs" exists
    When User "user1" requests list of users for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then There are "3" users returned
    When User "user3" requests list of users for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then There are "3" users returned

  Scenario: List of users with inactive user-customer or user-property relation
    Given Relation between user "user2" and customer with id "12300000-0000-4000-a000-000000000000" is inactivated
    When User "user1" requests list of users for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then There are "1" users returned
    Given Relation between user "user2" and customer with id "12300000-0000-4000-a000-000000000000" is activated
    When User "user1" requests list of users for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then There are "2" users returned
    Given Relation between user "user2" and property "defaultPropertyCode" is inactivated
    When GET request is sent to "/identity/users" on module "identity" by user "user1" for application version "versionWithSubscription"
    Then There are "1" users returned
