@Identity
Feature: Customers property sets access check feature (second level endpoints)
  - Checking when certain user should and should not have access to certain customers
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. customers/c_id/properties, properties/p_id/customers) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
  #      Creating default user just to be able to get property by code. Access checks are always steps 'by user'
    Given Default Snapshot user is created
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithCust1"
      | propertySetName | propertySetType |
      | prop_set1       | brand           |
      | prop_set2       | brand           |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithCust2"
      | propertySetName | propertySetType |
      | prop_set3       | brand           |


  Scenario: Second level entities - User sees only property sets he should for customer he sees
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"
#  DP-1677
    And Total count is "2"
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
    Then Response code is "404"
    When List of all property sets for customer with id "00000000-0000-4000-8000-123000000abc" is requested by user "userWithCust2"
    Then Response code is "200"
    And Total count is "1"

