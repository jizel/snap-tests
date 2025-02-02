#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from datetime import datetime, timedelta

from utils import *
from utils import thousand_range

TABLES = [
    {
        'table': 'dp.Dim_property',
        'special': False,
        'multiply': [1],
        'columns': [
            lambda data: data['property_id'],                                            # property_id
            lambda data: '990%03d99-9999-4999-a999-999999999999' % data['property_id'],  # property_key
            lambda data: 'Property name %03d' % data['property_id']                      # name
        ]
    },

    {
        'table': 'dp.Dim_wp_source',
        'special': False,
        'multiply': range(100),
        'columns': [
            lambda data: 100 * data['property_id'] + data['iter'],                # source_id
            lambda data: 'domain %d - %d' % (data['property_id'], data['iter']),  # source_name
        ]
    },

    {
        'table': 'dp.Fact_facebook_cumulative',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],       # property_id
            lambda data: data['iter'],              # date_id
            lambda data: increment(data, 2),        # followers
            lambda data: increment(data, 3),        # number_of_posts
            lambda data: increment(data, 4),        # engagements
            lambda data: increment(data, 5),        # impressions
            lambda data: increment(data, 6),        # reach
            lambda data: increment(data, 7),        # likes
            lambda data: increment(data, 8),        # unlikes
            lambda data: datetime.now(),            # collected_time_stamp
            lambda data: datetime.now(),            # inserted_time_stamp
        ]
    },

    {
        'table': 'dp.Fact_fb_post_stats',
        'special': False,
        'multiply': range(500),
        'columns': [
            lambda data: data['property_id'],                                                    # property_id
            lambda data: datetime.now() - timedelta(days=random.randint(data['iter'], 600)),     # post_date_time_added
            lambda data: data['iter'],                                                           # post_id
            lambda data: 'Post %d - %d' % (data['property_id'], data['iter']),                   # content
            lambda data: random.randint(10, 20000),                                              # reach
            lambda data: random.randint(1000, 10000),                                            # engagements
            lambda data: datetime.now(),                                                         # ins_upd_ts
        ]
    },

    {
        'table': 'dp.Fact_instagram_daily',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],                 # property_id
            lambda data: data['iter'],                        # date_id
            lambda data: increment(data, 2),                  # posts_count
            lambda data: increment(data, 3),                  # followers_count
            lambda data: increment(data, 4),                  # likes_sum
            lambda data: increment(data, 5),                  # comments_sum
            lambda data: increment(data, 6),                  # hashtag_count
            lambda data: 'hashtag_%d' % data['property_id'],  # hashtag_value
        ]
    },

    {
        'table': 'dp.Fact_twitter_daily',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],   # property_id
            lambda data: data['iter'],          # date_id
            lambda data: increment(data, 2),    # impressions
            lambda data: increment(data, 3),    # engagements
            lambda data: increment(data, 4),    # followers
            lambda data: increment(data, 5),    # number_of_posts
            lambda data: increment(data, 6),    # reach
            lambda data: increment(data, 7),    # retweets
            lambda data: increment(data, 8),    # retweet_reach
            lambda data: increment(data, 9),    # mentions
            lambda data: increment(data, 10),   # mention_reach
            lambda data: datetime.now(),        # inserted_time_stamp
        ]
    },

    {
        'table': 'dp.Fact_twitter_tweets',
        'special': False,
        'multiply': range(0, 100),
        'columns': [
            lambda data: data['property_id'],               # property_id
            lambda data: data['iter'],                      # tweet_id
            lambda data: datetime.now(),                    # date_time_added
            lambda data: "tweet %s" % data['property_id'],  # content
            lambda data: random.randint(0, 1000),           # impressions
            lambda data: random.randint(0, 1000),           # engagement
            lambda data: random.randint(0, 1000),           # retweet_count
            lambda data: random.randint(0, 1000),           # favorites
        ]
    },

    {
        'table': 'dp.Fact_ta_daily',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],       # property_id
            lambda data: data['iter'],              # date_id
            lambda data: random.randint(120, 160),  # index_rank
            lambda data: random.randint(120, 160),  # index_rank_total_number
            lambda data: rating(),                  # bubble_rating
            lambda data: increment(data, 5),        # number_of_reviews
            lambda data: random.randint(100, 200),  # rating_score_1
            lambda data: random.randint(100, 300),  # rating_score_2
            lambda data: random.randint(100, 400),  # rating_score_3
            lambda data: random.randint(100, 500),  # rating_score_4
            lambda data: random.randint(100, 600),  # rating_score_5
            lambda data: rating(),                  # rate_location
            lambda data: rating(),                  # rate_sleep
            lambda data: rating(),                  # rate_room
            lambda data: rating(),                  # rate_service
            lambda data: rating(),                  # rate_value
            lambda data: rating(),                  # rate_cleanliness
        ]
    },

    {
        'table': 'dp.Fact_ta_trip_type',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],       # property_id
            lambda data: random.randint(1, 6),      # trip_type_id
            lambda data: data['iter'],              # date_id
            lambda data: rating(),                  # bubble_rating
            lambda data: increment(data, 5),        # number_of_reviews
            lambda data: random.randint(100, 200),  # rating_score_1
            lambda data: random.randint(100, 300),  # rating_score_2
            lambda data: random.randint(100, 400),  # rating_score_3
            lambda data: random.randint(100, 500),  # rating_score_4
            lambda data: random.randint(100, 600),  # rating_score_5
            lambda data: rating(),                  # rate_location
            lambda data: rating(),                  # rate_sleep
            lambda data: rating(),                  # rate_room
            lambda data: rating(),                  # rate_service
            lambda data: rating(),                  # rate_value
            lambda data: rating(),                  # rate_cleanliness
        ]
    },

    {
        'table': 'dp.tripadvisor_geo_location',
        'special': True,
        'multiply': thousand_range,
        'columns': [
            lambda data: data['iter'],                   # location_id
            lambda data: 'town {}'.format(data['iter'])  # location_name
        ]
    },

    {
        'table': 'dp.tripadvisor_property',
        'special': True,
        'multiply': thousand_range,
        'columns': [
            lambda data: '990%03d99-9999-4999-a999-999999999999' % data['iter'],    # property_id
            lambda data: '59%04d' % data['iter'],                                   # property_location_id
            lambda data: random.randint(18, 21),                                    # geo_location_id
            lambda data: '0',                                                       # last_num_reviews
            lambda data: '2016-01-15 00:00:00'                                      # last_review_timestamp
        ]
    },

    {
        'table': 'dp.Fact_web_performance_cumulative',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],   # dim_property_id
            lambda data: data['iter'],          # dim_date_id
            lambda data: increment(data, 2),    # users
            lambda data: increment(data, 3),    # visits
            lambda data: increment(data, 4),    # transactions
            lambda data: increment(data, 5),    # revenue
            lambda data: datetime.now(),        # inserted_time_stamp
        ]
    },

    {
        'table': 'dp.Fact_web_performance',
        'special': False,
        'multiply': dates,
        'columns': [
            lambda data: data['property_id'],                                                        # dim_property_id
            lambda data: data['iter'],                                                               # dim_date_id
            lambda data: random.randint(1, 11),                                                      # dim_channel_id
            lambda data: random.randint(100 * data['property_id'], 100 * data['property_id'] + 99),  # dim_source_id
            lambda data: random.randint(1, 250),                                                     # dim_country_id
            lambda data: increment(data, 2),                                                         # users
            lambda data: increment(data, 3),                                                         # visits
            lambda data: increment(data, 4),                                                         # transactions
            lambda data: increment(data, 5),                                                         # revenue
            lambda data: datetime.now(),                                                             # inserted_time_stamp
        ]
    }
]
