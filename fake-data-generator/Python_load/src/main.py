#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv
import argparse
import os

import sys
from random import randint

import utils
from tables import TABLES


def main(table_filter, out_path, prop_size):
    # check if folder exist, if empty then exit
    if not os.path.exists(out_path):
        os.makedirs(out_path)

    print "Run after script ends:"
    print "mysql -h <host> -u <user> -p<password>"
    print 'SET SESSION SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";'

    for metadata in TABLES:
        if len(table_filter) and metadata['table'] not in table_filter:
            continue

        with open('{path}/{name}.tsv'.format(name=metadata['table'], path=out_path), 'wb') as csvfile:
            writer = csv.writer(csvfile, delimiter='\t')

            for property_id in prop_size:
                last_row = [0] * 20

                if metadata['special']:
                    fill_table(last_row, metadata, property_id, writer)
                    break

                fill_table(last_row, metadata, property_id, writer)

        print "LOAD DATA LOCAL INFILE '{table}.tsv' INTO TABLE {table} FIELDS TERMINATED BY '\\t';".format(table = metadata['table'])


def fill_table(last_row, metadata, property_id, writer):
    for iteration in metadata['multiply']:
        data = {
            'property_id': property_id,
            'iter': iteration,
            'row': last_row,
            'currency': utils.currency[randint(0, 2)]
        }

        last_row = [column(data) for column in metadata['columns']]
        writer.writerow(last_row)

def help():
    return '''Fake data generator for custom size of properties (default 20) for custom date (default 2017-01-01 to 2018-12-31).
    Output path attribute is optional parameter for specifying path for output files.
    Tables attribute is optional filter - coma separated e.g. table1,table2.
    By default, data for all tables are generated.'''


def create_custom_properties(number):
    if number < 0:
        sys.exit('Number for properties cannot be lower than 0!')
    return range(0, number)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='', usage=help())

    parser.add_argument('-out_path', type=str, default=os.getcwd(),
                        help='''Output path for generated files; examples: Windows: C:/Users/ Linux: /Users/''')

    parser.add_argument('-tables', type=str, help='''Table names, as string delimitered by "," ''')
    parser.add_argument('-prop_size', type=int, help='Number of properties for witch you want to generate data', default=20)
    parser.add_argument('-date_from', type=int, help='Year FROM data will be generated', default=2017)
    parser.add_argument('-date_to', type=int, help='Year TO data will be generated', default=2018)
    args = parser.parse_args()

    try:
        tables = args.tables.split(",")
    except:
        tables = []

    utils.dates_calculation(args.date_from, args.date_to)

    main(tables, args.out_path, create_custom_properties(args.prop_size))
