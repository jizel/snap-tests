Feature: twitter_metrics

  #Testing of api for twitter with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"
  #data in db are increasing for all metrics, inserted to db according to following pattern:
  Scenario Outline: Get twitter analytics data from API for a given wrong granularity
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <responce_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                 | granularity | responce_code | custom_code | property                             | since      | until      | content_type     |
      | /analytics/twitter                  | dd          | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/number_of_tweets | mm          | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/engagement       | yy          | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/followers        | 1dd         | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/impressions      | m1n         | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/reach            | 444         | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/retweets         | 6655665     | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/retweet_reach    | MONTH1      | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/mentions         | DAY3        | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |
      | /analytics/twitter/mention_reach    | WEEKs1      | 400           | 63          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <responce_code>
    And The metric count is <count>

    Examples: 
      | url                                 | granularity | count | since      | until      | property                             | responce_code | content_type     |
      | /analytics/twitter/number_of_tweets | day         | 801   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         | 7010  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         | 2103  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         | 3505  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         | 1402  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | day         | 1001  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | day         | 751   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | week        | 811   | 2015-12-05 | 2015-12-14 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | week        | 711   | 2015-12-06 | 2015-12-14 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | week        | 7110  | 2015-12-06 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | week        | 2133  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | week        | 3555  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | week        | 1422  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | week        | 711   | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | week        | 1011  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | week        | 761   | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | month       | 798   | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | month       | 698   | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | month       | 6980  | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | month       | 2094  | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | month       | 3490  | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | month       | 1396  | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | month       | 698   | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | month       | 998   | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | month       | 748   | 2015-11-01 | 2015-12-08 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Get specific analytics data from API for a given granularity for overall twitter metrics
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <responce_code>
    And Content type is "<content_type>"
    And Data is owned by "<data_owner>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values for all metrics

    Examples: 
      | url                | granularity | count | since      | until      | real_since | real_until | property                             | responce_code | content_type     | data_owner |
      | /analytics/twitter | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07 | 2015-11-23 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | day         | 90    | 2015-06-06 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |

  Scenario Outline: Get specific analytics data from API for a given granularity for particular twitter metric
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <responce_code>
    And Content type is "<content_type>"
    And Data is owned by "<data_owner>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values

    Examples: 
      | url                                 | granularity | count | since      | until      | real_since | real_until | property                             | responce_code | content_type     | data_owner |
      | /analytics/twitter/number_of_tweets | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03 | 2015-11-13 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | day         | 91    | 2015-06-07 | 2015-12-07 | 2015-09-09 | 2015-12-07 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | week        | 1     | 2015-11-08 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | week        | 1     | 2015-11-09 | 2015-11-16 | 2015-11-09 | 2015-11-15 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | week        | 3     | 2015-11-07 | 2015-11-30 | 2015-11-09 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | week        | 26    | 2015-01-07 | 2015-11-30 | 2015-06-01 | 2015-11-29 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01 | 2015-02-28 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/number_of_tweets | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/engagement       | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/followers        | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/impressions      | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/reach            | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweets         | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/retweet_reach    | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mentions         | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/mention_reach    | month       | 11    | 2013-02-01 | 2015-12-12 | 2015-01-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 | 200           | application/json | twitter    |

  Scenario Outline: Getting non-existent analytics data
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples: 
      | url                            | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics/twitter/not_present | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           | 151         |

  Scenario Outline: Getting miss mached non-existent analytics data
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples: 
      | url                      | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics/twitter/posts | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           | 151         |

  Scenario Outline: Checking error codes for analytics data
    When Get twitter "<url>" with missing property header
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                 | response_code | custom_code |
      | /analytics/twitter                  | 400           | 52          |
      | /analytics/twitter/number_of_tweets | 400           | 52          |
      | /analytics/twitter/engagement       | 400           | 52          |
      | /analytics/twitter/followers        | 400           | 52          |
      | /analytics/twitter/impressions      | 400           | 52          |
      | /analytics/twitter/reach            | 400           | 52          |
      | /analytics/twitter/retweets         | 400           | 52          |
      | /analytics/twitter/retweet_reach    | 400           | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <responce_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples: 
      | url                                 | granularity | since           | until      | count | property                             | responce_code                        | content_type     |
      | /analytics/twitter/number_of_tweets |             | 2015-12-03      | 2015-12-03 | 1     | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |
      | /analytics/twitter/engagement       | day         |                 | 2015-12-03 | 31    | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |
      | /analytics/twitter/followers        | day         | today - 1 month |            | 31    |                                      | 200              					   | application/json |
      | /analytics/twitter/impressions      | day         |                 |            | 31    | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |
      | /analytics/twitter/reach            |             |                 |            | 31    | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |
      | /analytics/twitter/retweets         |             | 2015-11-09      | 2015-11-22 | 14    | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |
      | /analytics/twitter/retweet_reach    |             | 2015-12-02      | 2015-12-02 | 1     | 99999999-9999-4999-a999-999999999999 | 200                                  | application/json |

  Scenario Outline: Get analytics data from API from 1800s
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <responce_code>
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"

    Examples: 
      | url                                 | granularity | start_date | end_date   | property                             | responce_code | content_type     | real_since	|	real_until	|
      | /analytics/twitter                  | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |	2015-01-01	|	2015-12-12	|
      | /analytics/twitter/number_of_tweets | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/engagement       | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/followers        | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/impressions      | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/reach            | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/retweets         | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/retweet_reach    | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter                  | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/number_of_tweets | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/engagement       | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/followers        | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/impressions      | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/reach            | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/retweets         | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|
      | /analytics/twitter/retweet_reach    | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |2015-01-01	|	2015-12-12	|

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <responce_code>
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains no more than <count> values

    Examples: 
      | url                                 | granularity | since          | until             | expected_granularity | expected_since    | expected_until | count | property                             | responce_code | content_type     |
      | /analytics/twitter/number_of_tweets |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/number_of_tweets | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         |             |                |                   | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | day         |                | today             | day                  | today - 1 month   | today          | 32    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | day         | today          |                   | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | week        |                | today             | week                 | today - 13 weeks  | today          | 13    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | week        | today          |                   | week                 | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | month       |                | today             | month                | today - 6 months  | today          | 6     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | month       | today          |                   | month                | today             | today          | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Checking number of values in response for various granularities
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <responce_code>
    And Response contains <count> values

    Examples: 
      | url                                 | granularity | since           | until | count | property                             | responce_code | content_type     |
      | /analytics/twitter/number_of_tweets | day         | today - 1 day   | today | 2     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/engagement       | day         | today - 6 days  | today | 7     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/followers        | day         | today - 7 days  | today | 8     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/impressions      | day         | today - 8 days  | today | 9     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/reach            | day         | today - 29 days | today | 30    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweets         | day         | today - 30 days | today | 31    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/retweet_reach    | week        | today           | today | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mentions         | week        | today           | today | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/mention_reach    | week        | today           | today | 0     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
