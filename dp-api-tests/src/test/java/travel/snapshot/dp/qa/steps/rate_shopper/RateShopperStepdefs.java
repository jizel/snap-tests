package travel.snapshot.dp.qa.steps.rate_shopper;

import cucumber.api.Transform;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.RateShopperSteps;

public class RateShopperStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private RateShopperSteps steps;
    
    @When("^Sending an empty request to \"([^\"]*)\"$")
    public void sending_an_empty_request_to(String url) throws Throwable {
        steps.emptyGetRequest(url);
    }

    @When("^Getting rate data for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\" fetched \"([^\"]*)\"$")
    public void getting_rate_data_for_since_until(String property_id, String since, String until, String fetched) {
        steps.getPropertyRateData(property_id, since, until, fetched);
    }
    
    @When("^Getting BAR values for a given market for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_market_rate_data_for_since_until(String property_id, String since, String until) {
       steps.getMarketRateData(property_id, since, until);
    }
<<<<<<< Upstream, based on origin/master
}
=======
    
    @When("^List of properties for market of \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(String propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getProperties(propertyId, limit, cursor);
    }
    
    @Then("^Response \"([^\"]*)\" for property \"([^\"]*)\" is \"([^\"]*)\"$")
    public void response_since_for_property(String fieldName, String propertyId, String value) {
       steps.dateFieldForProperty(fieldName, propertyId, value);
    }
}
>>>>>>> 2fb93fa Complete automated tests for the rate shopper API
