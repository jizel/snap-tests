Feature: instagram
  Testing of api for facebook with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"
  data in db are increasing for all metrics, inserted to db according to following pattern:
  starting from d=2014-01-01 as i=0, i++
  INSERT INTO `DP_SOCIAL_MEDIA`.`FactFacebookPageStats` (`dim_property_id`, `dim_date_id`, `impressions`, `engagements`, `followers`, `number_of_posts`, `reach`, `likes`, `unlikes`, `collected_time_stamp`, `inserted_time_stamp` )
  VALUES ( VALUES (999999, ${d.format("yyyyMMdd")},  ${i*3},  ${i},  ${i*10},  ${i+100}, ${i*5},  ${i*2}, ${i},  CURRENT_TIMESTAMP,   '${d.format("yyyy-MM-dd HH:mm:ss")}' );

  Scenario Outline: Get facebook analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples: 
      | url                                              | granularity |
      | /social_media/analytics/twitter                  | dd          |
      | /social_media/analytics/twitter/number_of_tweets | mm          |
      | /social_media/analytics/twitter/engagement       | yy          |
      | /social_media/analytics/twitter/followers        | 1dd         |
      | /social_media/analytics/twitter/impressions      | m1n         |
      | /social_media/analytics/twitter/reach            | 444         |
      | /social_media/analytics/twitter/retweets         | 6655665     |
      | /social_media/analytics/twitter/retweets_reach   | dasdasdsasa |
      | /social_media/analytics/twitter/mentions         | dsa         |
      | /social_media/analytics/twitter/mention_reach    | fffff       |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    And Content type is "application/json"
    And Response contains "<count>" values
    And Body contains entity with attribute "since" value "<since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "<granularity>"

    #need to add those | /social_media/analytics/twitter/mentions      | week | today | today | 0 |
    # need to add those | /social_media/analytics/twitter/mention_reach | week | today | today | 0 |
    Examples: 
      | url                                              | granularity | count | since      | until      |
      | /social_media/analytics/twitter                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/number_of_tweets | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/engagement       | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/followers        | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/impressions      | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/reach            | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/retweets         | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/retweets_reach   | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/mentions         | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter/mention_reach    | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /social_media/analytics/twitter                  | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/number_of_tweets | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/engagement       | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/followers        | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/impressions      | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/reach            | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets         | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets_reach   | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/mentions         | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/mention_reach    | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter                  | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/number_of_tweets | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/engagement       | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/followers        | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/impressions      | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/reach            | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets         | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets_reach   | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mentions         | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mention_reach    | day         | 23    | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter                  | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/number_of_tweets | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/engagement       | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/followers        | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/impressions      | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/reach            | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/retweets         | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/retweets_reach   | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/mentions         | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter/mention_reach    | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /social_media/analytics/twitter                  | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/number_of_tweets | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/engagement       | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/followers        | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/impressions      | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/reach            | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets         | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets_reach   | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/mentions         | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter/mention_reach    | week        | 1     | 2015-12-07 | 2015-12-13 |
      | /social_media/analytics/twitter                  | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/number_of_tweets | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/engagement       | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/followers        | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/impressions      | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/reach            | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets         | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/retweets_reach   | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/mentions         | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter/mention_reach    | week        | 1     | 2015-12-03 | 2015-12-13 |
      | /social_media/analytics/twitter                  | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/number_of_tweets | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/engagement       | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/followers        | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/impressions      | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/reach            | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets         | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets_reach   | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mentions         | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mention_reach    | week        | 2     | 2015-12-07 | 2015-12-23 |
      | /social_media/analytics/twitter                  | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/number_of_tweets | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/engagement       | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/followers        | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/impressions      | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/reach            | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets         | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/retweets_reach   | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mentions         | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter/mention_reach    | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /social_media/analytics/twitter                  | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/number_of_tweets | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/engagement       | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/followers        | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/impressions      | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/reach            | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/retweets         | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/retweets_reach   | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/mentions         | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter/mention_reach    | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /social_media/analytics/twitter                  | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/number_of_tweets | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/engagement       | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/followers        | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/impressions      | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/reach            | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/retweets         | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/retweets_reach   | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/mentions         | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter/mention_reach    | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /social_media/analytics/twitter                  | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/number_of_tweets | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/engagement       | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/followers        | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/impressions      | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/reach            | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/retweets         | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/retweets_reach   | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/mentions         | month       | 36    | 2013-02-01 | 2016-12-31 |
      | /social_media/analytics/twitter/mention_reach    | month       | 36    | 2013-02-01 | 2016-12-31 |

  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/instagram/not_present" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "52"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/social_media/analytics/instagram/tweets" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "52"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | error_code | custom_code |
      | /social_media/analytics/twitter                  | 400        | 52          |
      | /social_media/analytics/twitter/number_of_tweets | 400        | 52          |
      | /social_media/analytics/twitter/engagement       | 400        | 52          |
      | /social_media/analytics/twitter/followers        | 400        | 52          |
      | /social_media/analytics/twitter/impressions      | 400        | 52          |
      | /social_media/analytics/twitter/reach            | 400        | 52          |
      | /social_media/analytics/twitter/retweets         | 400        | 52          |
      | /social_media/analytics/twitter/retweets_reach   | 400        | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Response code is "200"
    And Content type is "application/json"
    And Response contains <count> values
    And Data is owned by "instagram"

    Examples: 
      | url                                              | granularity | start_date | end_date   | count |
      | /social_media/analytics/twitter/number_of_tweets |             | 2015-12-03 | 2015-12-03 | 1     |
      | /social_media/analytics/twitter/engagement       | day         |            | 2015-12-03 | 31    |
      | /social_media/analytics/twitter/followers        | day         | 2015-12-03 |            | 31    |
      | /social_media/analytics/twitter/impressions      | day         |            |            | 31    |
      | /social_media/analytics/twitter/reach            |             |            |            | 31    |
      | /social_media/analytics/twitter/retweets         |             | 2015-11-09 | 2015-11-02 | 1     |
      | /social_media/analytics/twitter/retweets_reach   |             | 2015-11-02 | 2015-12-02 | 1     |

  Scenario Outline: Get analytics data from API from 1800s
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "since" value "<start_date>"
    And Body contains entity with attribute "until" value "<end_date>"

    Examples: 
      | url                                              | granularity | start_date | end_date   |
      | /social_media/analytics/twitter                  | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/number_of_tweets | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/engagement       | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/followers        | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/impressions      | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/reach            | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/retweets         | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter/retweets_reach   | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/twitter                  | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/number_of_tweets | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/engagement       | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/followers        | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/impressions      | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/reach            | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/retweets         | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/twitter/retweets_reach   | day         | 1888-09-01 | 1888-09-01 |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains no more than <count> values

    Examples: 
      | url                                              | granularity | start_date     | end_date          | expected_granularity | expected_since    | expected_until | count |
      | /social_media/analytics/twitter/number_of_tweets |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/number_of_tweets |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/number_of_tweets | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/number_of_tweets | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/number_of_tweets | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/number_of_tweets | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/number_of_tweets | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/number_of_tweets | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/number_of_tweets | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/number_of_tweets | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/number_of_tweets | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/number_of_tweets | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/engagement       |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/engagement       |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/engagement       | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/engagement       | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/engagement       | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/engagement       | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/engagement       | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/engagement       | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/engagement       | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/engagement       | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/engagement       | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/engagement       | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/followers        |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/followers        |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/followers        | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/followers        | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/followers        | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/followers        | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/followers        | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/followers        | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/followers        | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/followers        | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/followers        | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/followers        | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/impressions      |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/impressions      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/impressions      | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/impressions      | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/impressions      | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/impressions      | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/impressions      | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/impressions      | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/impressions      | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/impressions      | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/impressions      | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/impressions      | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/reach            |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/reach            |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/reach            | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/reach            | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/reach            | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/reach            | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/reach            | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/reach            | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/reach            | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/reach            | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/reach            | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/reach            | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/retweets         |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/retweets         |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/retweets         | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/retweets         | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/retweets         | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/retweets         | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/retweets         | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/retweets         | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/retweets         | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/retweets         | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/retweets         | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/retweets         | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/retweets_reach   |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/retweets_reach   |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/twitter/retweets_reach   | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/twitter/retweets_reach   | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/twitter/retweets_reach   | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/twitter/retweets_reach   | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/twitter/retweets_reach   | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/twitter/retweets_reach   | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/twitter/retweets_reach   | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/twitter/retweets_reach   | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/twitter/retweets_reach   | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/twitter/retweets_reach   | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |

  # Scenario: Get data owners data for facebook
  #can be combined to other scnearios
  #  When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
  #  Then Content type is "application/json"
  #  And Response code is "200"
  #  And Data is owned by "facebook"
  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values

    Examples: 
      | url                                              | granularity | since           | until | count |
      #this one is different - returns all metrics together, so validation of number of values needs to be different
      | /social_media/analytics/twitter/number_of_tweets | day         | today - 1 day   | today | 2     |
      | /social_media/analytics/twitter/engagement       | day         | today - 6 days  | today | 7     |
      | /social_media/analytics/twitter/followers        | day         | today - 7 days  | today | 8     |
      | /social_media/analytics/twitter/impressions      | day         | today - 8 days  | today | 9     |
      | /social_media/analytics/twitter/reach            | day         | today - 29 days | today | 30    |
      | /social_media/analytics/twitter/retweets         | day         | today - 30 days | today | 31    |
      | /social_media/analytics/twitter/retweets_reach   | week        | today           | today | 0     |
      | /social_media/analytics/twitter/mentions         | week        | today           | today | 0     |
      | /social_media/analytics/twitter/mention_reach    | week        | today           | today | 0     |
