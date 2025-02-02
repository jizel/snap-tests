package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Facebook pages Noon ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Facebook data collection.
 * All values are incremental, with the exception of values for followers, which are total
 * 
 * This is the path of the data for noon ETLs:
 * Facebook (3x/day)-> Raw data table (2x/day)-> Staging table (2x/day)-> Intermediate table (1x/day)-> Fact table for noon runs
 * 
 * The following transfers are covered by tests:
 * Raw data table -> Staging table
 * Staging table -> Fact table for noon runs
 * 
 * Noon ETLs occur at 12:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
*/

public class TestFacebookPagesNoonETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesNoonETL.class);

    @Test
    public void testStagingLoad() throws Exception {
    	//The noon ETL for each hotel is triggered after the third run to get data from the Facebook API has passed
        String sqlQueryForSource = 
        		"select count(*) "
        		+ "from ("
        		+ "select * "
        		+ "from RawImportedFacebookPageStatistics t "
        		+ "where t.date=curdate() - interval 1 day "
        		+ "and t.data_collection_run = 3 "
        		+ "group by property_id) tt";
        String sqlQueryForTarget =
        		"select count(*) "
        		+ "from ("
        		+ "select * "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "where t.date=curdate() - interval 1 day "
        		+ "group by property_id) tt";

        logger.info("\nStart control checks on table 'IncrementalFacebookPageStatistics'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        
        List<String> followUpListToSource = new ArrayList<String>();
        //Separate checks for followers are used, since the values are total, instead of incremental
        followUpListToSource.add("select coalesce(sum(total_followers), 0) from RawImportedFacebookPageStatistics where date = curdate() - interval 1 day and data_collection_run = 3");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_number_of_posts - r1.incremental_number_of_posts + r3.incremental_number_of_posts), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_engagements - r1.incremental_engagements + r3.incremental_engagements), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_impressions - r1.incremental_impressions + r3.incremental_impressions), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_reached - r1.incremental_reached + r3.incremental_reached), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_likes - r1.incremental_likes + r3.incremental_likes), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
        followUpListToSource.add(
        		"select coalesce(sum(r2.incremental_unlikes - r1.incremental_unlikes + r3.incremental_unlikes), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate() - interval 1 day");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add(
        		"select coalesce(sum(total_followers), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_number_of_posts), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_engagements), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_impressions), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_reached), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_likes), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_unlikes), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("Metric: followers");
        metrics.add("Metric: number of posts");
        metrics.add("Metric: engagement");
        metrics.add("Metric: impressions");
        metrics.add("Metric: reach");
        metrics.add("Metric: likes");
        metrics.add("Metric: unlikes");
        
        followUpLoadTest(followUpListToSource, followUpListToTarget, metrics);
    }

    @Test
    public void testFactLoad() throws Exception {
    	//The transfer to the fact table is triggered immediately after the transfer to the staging table has finished
        String sqlQueryForSource = "select count(*) from IncrementalFacebookPageStatistics where date = curdate() - interval 1 day";
        String sqlQueryForTarget = "select count(*)*2 from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day)+0";

        //Separate checks for followers are used, since the values are total, instead of incremental
        String sqlQueryForSourceFollowers = 
        		"select coalesce(sum(total_followers), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date <= date_sub(curdate(), interval 1 day) "
        		+ "and total_followers is not null "
        		+ "and time_stamp < addtime(curdate() - interval 1 day, '19:00') " // before the runs for the next day start (20:01 server time) 
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp";
        String sqlQueryForTargetFollowers = "select coalesce(sum(followers), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day)+0";
        
        logger.info("\nStart control checks on table 'T_FactFacebookPageStatsCurrDay'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        testLoad(sqlQueryForSourceFollowers, sqlQueryForTargetFollowers, "Metric: followers");
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add(
        		"select coalesce(sum(number_of_posts), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and number_of_posts is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(engagements), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and engagements is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(impressions), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and impressions is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(reach), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and reach is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(likes), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and likes is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(unlikes), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 2 day) + 0 "
        		+ "and unlikes is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_number_of_posts,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_engagements,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_impressions,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_reached,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_likes,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_unlikes,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = tt.min_time_stamp"); 
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select coalesce(sum(number_of_posts), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select coalesce(sum(engagements), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select coalesce(sum(impressions), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select coalesce(sum(reach), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select coalesce(sum(likes), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select coalesce(sum(unlikes), 0) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("number of posts");
        metrics.add("engagement");
        metrics.add("impressions");
        metrics.add("reach");
        metrics.add("likes");
        metrics.add("unlikes");
        
        verifyLoad(factsYesterdayList, incrementalsTodayList, factsTodayList, metrics);
        
    }

}