Feature: Review multiproperty customer property
  #TODO add paging tests - currently paging is only prepared but not implemented
  #TODO request with invalid access token ?

  Background:
    # 5 property, 1 customer, 1 user, with all needed relations
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | id                                   | type     | username     | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser | Snapshot  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | anchorCustomerId                     |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000399-9999-4999-a999-999999999999 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between user "snapshotUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with isPrimary "true"
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p2_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p3_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between user "snapshotUser" and property with code "p1_code" exists
    Given Relation between user "snapshotUser" and property with code "p2_code" exists
    Given Relation between user "snapshotUser" and property with code "p3_code" exists


#------------
# GET /review/analytics/customer/{customer_id}/popularity_index_rank

  Scenario Outline: Get Review analytics data from API for a given wrong granularity, since, until, limit and cursor
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "<limit>" and cursor "<cursor>"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "40002"

    Examples:
      | metric                | customer_id                          | granularity | since      | until      | limit        | cursor       |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

  Scenario Outline: Checking mandatory values
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 400
    And Custom code is 40002
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."

    Examples:
      | metric                | customer_id                          | since      | until      | granularity | details                                       |
#  DP-1991
#      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | 2015-12-03 | /null       | Mandatory parameter 'granularity' is missing. |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | /null      | day         | Mandatory parameter 'until' is missing.       |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | /null      | 2015-12-03 | week        | Mandatory parameter 'since' is missing.       |
#  DP-1991
#      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | 2015-12-03 | /null       | Mandatory parameter 'granularity' is missing. |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | /null      | day         | Mandatory parameter 'until' is missing.       |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | /null      | 2015-12-03 | week        | Mandatory parameter 'since' is missing.       |
#  DP-1991
#      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | 2015-12-03 | /null       | Mandatory parameter 'granularity' is missing. |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | /null      | day         | Mandatory parameter 'until' is missing.       |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | /null      | 2015-12-03 | week        | Mandatory parameter 'since' is missing.       |
#  DP-1991
#      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | 2015-12-03 | /null       | Mandatory parameter 'granularity' is missing. |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 2015-12-03 | /null      | day         | Mandatory parameter 'until' is missing.       |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | /null      | 2015-12-03 | week        | Mandatory parameter 'since' is missing.       |


  Scenario Outline: Get specific amount of analytics data from API for a given granularity
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains 3 properties
    And Response properties contains "<count>" values

    Examples:
      | metric                | customer_id                          | granularity | count | since             | until | real_since        | real_until |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1     | today             | today | today             | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1     | today             | today | today             | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1     | today             | today | today             | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1     | today             | today | today             | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 13    | today - 12 months | today | today - 12 months | today      |

  Scenario Outline: Checking data corectness for popularity_index_rank
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for popularity index
    Examples:
      | metric                | json_input_file                                     | customer_id                          | granularity | since      | until      |
      | popularity_index_rank | /multiproperty/customer/popularity_index_day.json   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/customer/popularity_index_week.json  | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-12 | 2015-12-03 |


  Scenario Outline: Checking data corectness for aspects_of_business
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for aspects of business

    Examples:
      | metric              | json_input_file                                        | customer_id                          | granularity | since      | until      |
      | aspects_of_business | /multiproperty/customer/aspects_of_business_day.json   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 |
      | aspects_of_business | /multiproperty/customer/aspects_of_business_week.json  | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-12 | 2015-12-03 |
      | aspects_of_business | /multiproperty/customer/aspects_of_business_month.json | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for number_of_reviews
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for number of reviews

    Examples:
      | metric            | json_input_file                                      | customer_id                          | granularity | since      | until      |
      | number_of_reviews | /multiproperty/customer/number_of_reviews_day.json   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 |
      | number_of_reviews | /multiproperty/customer/number_of_reviews_week.json  | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-12 | 2015-12-03 |
      | number_of_reviews | /multiproperty/customer/number_of_reviews_month.json | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for overall_bubble_rating
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for overall bubble rating

    Examples:
      | metric                | json_input_file                               | customer_id                          | granularity | since      | until      |
      | overall_bubble_rating | /multiproperty/customer/bubble_for_day.json   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-03 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/customer/bubble_for_week.json  | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-12 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/customer/bubble_for_month.json | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Response contains 0 values of attribute named "properties"

    Examples:
      | metric                | customer_id                          | granularity | since      | until      |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1880-12-01 | 1880-12-03 |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 1880-11-01 | 1880-12-03 |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 1880-10-01 | 1880-12-03 |

      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1880-12-01 | 1880-12-03 |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 1880-11-01 | 1880-12-03 |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 1880-10-01 | 1880-12-03 |

      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1880-12-01 | 1880-12-03 |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 1880-11-01 | 1880-12-03 |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 1880-10-01 | 1880-12-03 |

      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 1880-12-01 | 1880-12-03 |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 1880-11-01 | 1880-12-03 |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 1880-10-01 | 1880-12-03 |


    #  DP-1495 fixed. Details now specified in details field which cannot be parsed. Add details checking when such parsing functions is ready. Not a prio now.
  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get "<metric>" for list of properties for customer "<customer_id>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 400
    And Custom code is 40002
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."

    Examples:
      | metric                | customer_id                          | granularity | until      | since      | details                                                                                                                                                               |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-12-02) |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-11-02) |
      | popularity_index_rank | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-10-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-10-02) |

      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-12-02) |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-11-02) |
      | aspects_of_business   | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-10-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-10-02) |

      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-12-02) |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-11-02) |
      | number_of_reviews     | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-10-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-10-02) |

      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | day         | 2015-12-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-12-02) |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | week        | 2015-11-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-11-02) |
      | overall_bubble_rating | 1238fd9a-a05d-42d8-8e84-42e904ace123 | month       | 2015-10-02 | 2015-12-03 | The date specified in the 'since' query parameter (2015-12-03) is after the date specified in the 'until' query parameter (2015-10-02) |
