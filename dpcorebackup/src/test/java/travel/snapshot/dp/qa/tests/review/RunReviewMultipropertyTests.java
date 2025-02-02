package travel.snapshot.dp.qa.cucumber.tests.review;

import net.serenitybdd.cucumber.CucumberWithSerenity;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/nonPms/review/multiproperty", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunReviewMultipropertyTests {

}
