package travel.snapshot.dp.qa;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.Ignore;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/configuration", tags = {"~@skipped"})
public class RunConfigurationTests {

}