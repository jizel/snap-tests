package travel.snapshot.dp.qa.steps.identity.api_subscriptions;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.api.identity.subscription.model.ApiSubscriptionDto;
import travel.snapshot.dp.api.identity.subscription.model.ApiSubscriptionUpdateDto;
import travel.snapshot.dp.qa.serenity.api_subscriptions.ApiSubscriptionSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

/**
 * Created by vlcek on 3/17/2016.
 */
public class ApiSubscriptionStepsdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private ApiSubscriptionSteps apiSteps;


    @Given("^The following api subscriptions exist$")
    public void theFollowingApiSubscriptionsExist(List<ApiSubscriptionDto> listApiSubscriptions) throws Throwable {
        apiSteps.followingApiSubscriptionExist(listApiSubscriptions);
    }

    @Given("^The following api subscriptions is created$")
    public void theFollowingApiSubscriptionsIsCreated(List<ApiSubscriptionDto> listApiSubscriptions) throws Throwable {
       apiSteps.followingApiSubscriptionIsCreated(listApiSubscriptions.get(0));
    }

    @When("^Api subscription with id \"([^\"]*)\" is got$")
    public void apiSubscriptionWithIdIsGot(String apiSubscriptionId) throws Throwable {
        apiSteps.apiWithIdIsGot(apiSubscriptionId);
    }

    @When("^Api subscription with id \"([^\"]*)\" is got with etag$")
    public void apiSubscriptionWithIdIsGotWithEtag(String apiSubscriptionId) throws Throwable {
        apiSteps.apiWithIdIsGotWithEtag(apiSubscriptionId);
    }

    @When("^Trying to create second api subscription with the same versionID$")
    public void tryingToCreateSecondApiSubscriptionWithTheSameVersionID() throws Throwable {
        apiSteps.createExistingApiSubscription();
    }

    @When("^Api subscription with id \"([^\"]*)\" is activated$")
    public void apiSubscriptionWithIdIsActivated(String apiSubscriptionId) throws Throwable {
        apiSteps.setActivateFieldApiSubscription(apiSubscriptionId, true);
    }

    @When("^Api subscription with id \"([^\"]*)\" is deactivated$")
    public void apiSubscriptionWithIdIsDeactivated(String apiSubscriptionId) throws Throwable {
        apiSteps.setActivateFieldApiSubscription(apiSubscriptionId, false);
    }

    @When("^Api subscription with id \"([^\"]*)\" is deleted$")
    public void apiSubscriptionWithIdIsDeleted(String apiSubscriptionId) throws Throwable {
        apiSteps.deleteApiSubscription(apiSubscriptionId);
    }

    @When("^Api subscription with id \"([^\"]*)\" is updated with following data$")
    public void apiSubscriptionWithIdIsUpdatedWithFollowingData(String apiSubscriptionId, List<ApiSubscriptionUpdateDto> updateData) throws Throwable {
        apiSteps.updateApiSubscription(apiSubscriptionId, updateData);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is among all api subscriptions$")
    public void apiSubscriptionWithIdIsAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, true);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is not among all api subscriptions$")
    public void apiSubscriptionWithIdIsNotAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, false);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is active$")
    public void apiSubscriptionWithIdIsActive(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionActivity(apiSubscriptionId, true);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is not active$")
    public void apiSubscriptionWithIdIsNotActive(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionActivity(apiSubscriptionId, false);
    }
}
