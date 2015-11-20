Feature: Users_get

  Background:
    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User1    | def2@snapshot.travel | UTC+01:00 | cz      |

  Scenario: Getting user
    When User with username "default1" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_id"

    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "default1"
    And Body contains entity with attribute "first_name" value "Default1"
    And Body contains entity with attribute "last_name" value "User1"
    And Body contains entity with attribute "email" value "def1@snapshot.travel"
    And Body contains entity with attribute "timezone" value "UTC+01:00"
    And Body contains entity with attribute "culture" value "cz"

  Scenario: Getting user with etag
    When User with username "default1" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting user with not current etag
  User is got, etag is saved to tmp, then user culture is updated to "sk" so etag should change and is got again with previous etag

    When User with username "default1" is got for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_id"

    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "default1"
    And Body contains entity with attribute "first_name" value "Default1"
    And Body contains entity with attribute "last_name" value "User1"
    And Body contains entity with attribute "email" value "def1@snapshot.travel"
    And Body contains entity with attribute "timezone" value "UTC+01:00"
    And Body contains entity with attribute "culture" value "sk"

  Scenario: Checking error code for getting user
    When Nonexistent user id is got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of users
    Given The following users exist
      | userType | userName        | firstName     | lastName   | email                       | timezone  | culture |
      | customer | list_default_1  | ListDefault1  | ListUser1  | list_user1@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_2  | ListDefault2  | ListUser2  | list_user2@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_3  | ListDefault3  | ListUser3  | list_user3@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_4  | ListDefault4  | ListUser4  | list_user4@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_5  | ListDefault5  | ListUser5  | list_user5@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_6  | ListDefault6  | ListUser6  | list_user6@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_7  | ListDefault7  | ListUser7  | list_user7@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_8  | ListDefault8  | ListUser8  | list_user8@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_9  | ListDefault9  | ListUser9  | list_user9@snapshot.travel  | UTC+01:00 | cz      |
      | customer | list_default_10 | ListDefault10 | ListUser10 | list_user10@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_11 | ListDefault11 | ListUser11 | list_user11@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_12 | ListDefault12 | ListUser12 | list_user12@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_13 | ListDefault13 | ListUser13 | list_user13@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_14 | ListDefault14 | ListUser14 | list_user14@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_15 | ListDefault15 | ListUser15 | list_user15@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_16 | ListDefault16 | ListUser16 | list_user16@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_17 | ListDefault17 | ListUser17 | list_user17@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_18 | ListDefault18 | ListUser18 | list_user18@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_19 | ListDefault19 | ListUser19 | list_user19@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_20 | ListDefault20 | ListUser20 | list_user20@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_21 | ListDefault21 | ListUser21 | list_user21@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_22 | ListDefault22 | ListUser22 | list_user22@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_23 | ListDefault23 | ListUser23 | list_user23@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_24 | ListDefault24 | ListUser24 | list_user24@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_25 | ListDefault25 | ListUser25 | list_user25@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_26 | ListDefault26 | ListUser26 | list_user26@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_27 | ListDefault27 | ListUser27 | list_user27@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_28 | ListDefault28 | ListUser28 | list_user28@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_29 | ListDefault29 | ListUser29 | list_user29@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_30 | ListDefault30 | ListUser30 | list_user30@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_31 | ListDefault31 | ListUser31 | list_user31@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_32 | ListDefault32 | ListUser32 | list_user32@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_33 | ListDefault33 | ListUser33 | list_user33@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_34 | ListDefault34 | ListUser34 | list_user34@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_35 | ListDefault35 | ListUser35 | list_user35@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_36 | ListDefault36 | ListUser36 | list_user36@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_37 | ListDefault37 | ListUser37 | list_user37@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_38 | ListDefault38 | ListUser38 | list_user38@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_39 | ListDefault39 | ListUser39 | list_user39@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_40 | ListDefault40 | ListUser40 | list_user40@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_41 | ListDefault41 | ListUser41 | list_user41@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_42 | ListDefault42 | ListUser42 | list_user42@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_43 | ListDefault43 | ListUser43 | list_user43@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_44 | ListDefault44 | ListUser44 | list_user44@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_45 | ListDefault45 | ListUser45 | list_user45@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_46 | ListDefault46 | ListUser46 | list_user46@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_47 | ListDefault47 | ListUser47 | list_user47@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_48 | ListDefault48 | ListUser48 | list_user48@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_49 | ListDefault49 | ListUser49 | list_user49@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_50 | ListDefault50 | ListUser50 | list_user50@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_51 | ListDefault51 | ListUser51 | list_user51@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_52 | ListDefault52 | ListUser52 | list_user52@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_53 | ListDefault53 | ListUser53 | list_user53@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_54 | ListDefault54 | ListUser54 | list_user54@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_55 | ListDefault55 | ListUser55 | list_user55@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_56 | ListDefault56 | ListUser56 | list_user56@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_57 | ListDefault57 | ListUser57 | list_user57@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_58 | ListDefault58 | ListUser58 | list_user58@snapshot.travel | UTC+01:00 | cz      |
      | customer | list_default_59 | ListDefault59 | ListUser59 | list_user59@snapshot.travel | UTC+01:00 | cz      |

    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned

    Examples:
      | limit | cursor | returned |
      | /null |        | 50       |
      | /null | /null  | 50       |
      |       |        | 50       |
      |       | /null  | 50       |
      | 15    |        | 15       |
      |       | 1      | 50       |
      | 20    | 0      | 20       |
      | 10    | 0      | 10       |
      | 5     | 5      | 5        |

    #TODO test filter, sort with different values

  Scenario Outline: Checking error codes for getting list of users
    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter      | sort         | sort_desc    | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null       | /null        | /null        | 400           | 63          |
      |       | -1     | /null       | /null        | /null        | 400           | 63          |
      | /null | text   | /null       | /null        | /null        | 400           | 63          |
      |       | text   | /null       | /null        | /null        | 400           | 63          |
      | -1    |        | /null       | /null        | /null        | 400           | 63          |
      | -1    | /null  | /null       | /null        | /null        | 400           | 63          |
      | text  |        | /null       | /null        | /null        | 400           | 63          |
      | text  | /null  | /null       | /null        | /null        | 400           | 63          |
      | 10    | -1     | /null       | /null        | /null        | 400           | 63          |
      | text  | 0      | /null       | /null        | /null        | 400           | 63          |
      | 10    | text   | /null       | /null        | /null        | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null       | company_name | company_name | 400           | 64          |
      | 10    | 0      | /null       | /null        | nonexistent  | 400           | 63          |
      | 10    | 0      | /null       | nonexistent  | /null        | 400           | 63          |
      #| 10    | 0      | /null    | company_name |              | 400           | 63          |
      #| 10    | 0      | /null    |              | company_name | 400           | 63          |
      #| 10    | 0      | /null    | /null        |              | 400           | 63          |
      #| 10    | 0      | /null    |              | /null        | 400           | 63          |
      #| 10    | 0      | /null    |              |              | 400           | 63          |
      | 10    | 0      | user_name== | /null        | /null        | 400           | 63          |
      | 10    | 0      | aaa==CZ*    | /null        | /null        | 400           | 63          |

  Scenario Outline: Filtering list of users
    Given The following users exist
      | userType | userName         | firstName      | lastName    | email                        | phone        | timezone  | culture |
      | customer | filter_default_1 | FilterDefault1 | FilterUser1 | filter_user1@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_default_2 | FilterDefault2 | FilterUser2 | filter_user2@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | guest    | filter_default_3 | FilterDefault3 | FilterUser3 | filter_user3@snapshot.travel | +42010111213 | UTC+04:00 | cz      |
      | customer | filter_default_4 | FilterDefault4 | FilterUser4 | filter_user4@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | partner  | filter_default_5 | FilterDefault5 | FilterUser5 | filter_user5@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_default_6 | FilterDefault6 | FilterUser6 | filter_user6@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | other_default_7  | FilterDefault7 | FilterUser7 | filter_user7@snapshot.travel | +42010111218 | UTC+01:00 | cz      |
      | customer | other_default_8  | FilterDefault8 | FilterUser8 | filter_user8@snapshot.travel | +42010111213 | UTC+01:00 | sk      |
      | snapshot | other_default_9  | FilterDefault9 | FilterUser9 | filter_user9@snapshot.travel | +42010111213 | UTC+01:00 | sk      |

    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are users with following usernames returned in order: <expected_usernames>

    Examples:
      | limit | cursor | returned | filter                                     | sort      | sort_desc | expected_usernames                                                                       |
      | 5     | 0      | 5        | user_name=='filter_default*'               | user_name |           | filter_default_1, filter_default_2, filter_default_3, filter_default_4, filter_default_5 |
      | 5     | 0      | 5        | user_name=='filter_default*'               |           | user_name | filter_default_6, filter_default_5, filter_default_4, filter_default_3, filter_default_2 |
      | 5     | 2      | 4        | user_name=='filter_default*'               | user_name |           | filter_default_3, filter_default_4, filter_default_5, filter_default_6                   |
      | 5     | 2      | 4        | user_name=='filter_default*'               |           | user_name | filter_default_4, filter_default_3, filter_default_2, filter_default_1                   |
      | /null | /null  | 1        | user_name==filter_default_6                | /null     | /null     | filter_default_6                                                                         |
      | /null | /null  | 2        | user_name==other_default_* and culture==sk | user_name | /null     | other_default_8, other_default_9                                                         |
      | /null | /null  | 1        | user_type==snapshot                        | /null     | /null     | other_default_9                                                                          |
      | /null | /null  | 1        | email==filter_user4@snapshot.travel        | /null     | /null     | filter_default_4                                                                         |
      | /null | /null  | 1        | timezone==UTC+04:00                        | /null     | /null     | filter_default_3                                                                         |
      | /null | /null  | 1        | phone==+42010111218                        | /null     | /null     | other_default_7                                                                          |
  #add all fields