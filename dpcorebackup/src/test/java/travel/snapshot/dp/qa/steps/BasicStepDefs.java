package travel.snapshot.dp.qa.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.seleniumhq.jetty9.util.StringUtil.isNotBlank;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.bodyContainsEntityWith;
import static travel.snapshot.dp.qa.serenity.BasicSteps.bodyIsEmpty;
import static travel.snapshot.dp.qa.serenity.BasicSteps.contentTypeIs;
import static travel.snapshot.dp.qa.serenity.BasicSteps.customCodeIs;
import static travel.snapshot.dp.qa.serenity.BasicSteps.responseCodeIs;
import static travel.snapshot.dp.qa.serenity.BasicSteps.sendBlankPost;
import static travel.snapshot.dp.qa.serenity.BasicSteps.sendPostWithBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang3.StringUtils;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.UUID;

public class BasicStepDefs {

    @Steps
    private BasicSteps basicSteps;
    @Steps
    private UsersSteps usersSteps;
    @Steps
    private ApplicationVersionsSteps applicationVersionSteps;

    @Then("^Response code is (\\d+)$")
    public void response_code_is(int responseCode) throws Throwable {
        responseCodeIs(responseCode);
    }

    @Then("^Response code is \"([^\"]*)\"$")
    public void response_code_is_string(String responseCode) throws Throwable {
        responseCodeIs(Integer.valueOf(responseCode));
    }

    @When("^\"([^\"]*)\" is called without token using \"([^\"]*)\"$")
    public void is_called_without_token_using(String service, String method) throws Throwable {
        basicSteps.isCalledWithoutTokenUsingMethod(service, method);
    }

    @Then("^Custom code is (\\d+)$")
    public void Custom_code_is(Integer customCode) throws Throwable {
        customCodeIs(customCode);
    }

    @Then("^Custom code is \"([^\"]*)\"$")
    public void custom_code_is(Integer customCode) throws Throwable {
        customCodeIs(customCode);
    }

    @Then("^Content type is \"([^\"]*)\"$")
    public void content_type_is(String contentType) throws Throwable {
        contentTypeIs(contentType);
    }

    @Then("^Body is empty$")
    public void body_is_empty() throws Throwable {
        bodyIsEmpty();
    }

    @Then("^Etag header is present$")
    public void Etag_header_is_present() throws Throwable {
        basicSteps.etagIsPresent();
    }

    @When("^File \"([^\"]*)\" is used for \"([^\"]*)\" to \"([^\"]*)\" on \"([^\"]*)\"$")
    public void File_is_used_for_to_on(String filename, String method, String url, String module) throws Throwable {
        basicSteps.useFileForSendDataTo(filename, method, url, module);
    }

    @When("^Empty POST request is sent to \"([^\"]*)\" on module \"([^\"]*)\"$")
    public void emptyPOSTRequestIsSentToOn(String url, String module) throws Throwable {
        sendBlankPost(url, module);
    }

    @When("^POST request is sent to \"([^\"]*)\" on module \"([^\"]*)\" with$")
    public void post_request_is_sent_to_on_module_with(String url, String module, DataTable contents) throws Throwable {
        HashMap<String, String> contentsMap = new HashMap<String, String>();
        for (DataTableRow row: contents.getGherkinRows()) {
            contentsMap.put(row.getCells().get(0), row.getCells().get(1));
        }
        String body = new ObjectMapper().writeValueAsString(contentsMap);
        sendPostWithBody(url, module, body);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_entity_with_attribute_value(String atributeName, String value) throws Throwable {
        bodyContainsEntityWith(atributeName, value);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\" and integer value (\\d+)$")
    public void Body_contains_entity_with_attribute_value(String atributeName, Integer value) throws Throwable {
        bodyContainsEntityWith(atributeName, value);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\"$")
    public void Body_contains_entity_with_attribute(String atributeName) throws Throwable {
        bodyContainsEntityWith(atributeName);
    }

    @Then("^Body doesn't contain entity with attribute \"([^\"]*)\"$")
    public void Body_doesnt_contains_entity_with_attribute(String atributeName) throws Throwable {
        basicSteps.bodyDoesntContainEntityWith(atributeName);
    }

    @Then("^Link header is '(.*)'$")
    public void Link_header_is(String linkHeader) throws Throwable {
        basicSteps.headerIs("Link", linkHeader);
    }

    @Then("^Total count is \"(.*)\"$")
    public void Total_count_is_total(String total) throws Throwable {
        basicSteps.headerIs("X-Total-Count", total);
    }

    @Then("^Response contains (\\d+) values of attribute named \"([^\"]*)\"$")
    public void responseContainsPropertiesWithAttribute(int count, String attributeName) throws Throwable {
        basicSteps.responseContainsNoOfAttributes(count, attributeName);
    }

    @And("^The \"([^\"]*)\" attribute in response contains only CAPITAL latin characters or numbers$")
    public void theAttributeInResponseContainsOnlyCAPITALLatinCharactersOrNumbers(String attributeName) throws Throwable {
        String attributeValue = basicSteps.getAttributeValue(attributeName);

        assertThat("Attribute " + attributeName + " is blank", isNotBlank(attributeValue), is(true));
        assertThat("Attribute " + attributeName + " contains white spaces: " + attributeValue,
                StringUtils.containsWhitespace(attributeValue), is(false));
        assertThat("Attribute " + attributeName + " is not sequence of capital latin letters and digits. It is: "+ attributeValue,
                attributeValue.matches("[A-Z0-9]+"), is(true));
    }

    @When("^GET request is sent to \"([^\"]*)\"(?: on module \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?(?: with since \"([^\"]*)\", until \"([^\"]*)\", granularity \"([^\"]*)\" and property \"([^\"]*)\")?( in path)?$")
    public void getRequestIsSentToOnModule(String url, String module, String username, String applicationVersionName,
                                           String since, String until, String granularity, String property, String asParam) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        basicSteps.sendGetRequestToUrlByUserForAppWithParams(userId, applicationVersionId, url, module, since, until, granularity, property, asParam);
    }

    @When("^GET request is sent to \"([^\"]*)\"(?: on module \"([^\"]*)\")? without X-Auth-UserId header$")
    public void getRequestIsSentToOnModuleWithoutXAuthUserIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlWithoutUserHeader(url, module);
    }

    @When("^GET request is sent to \"([^\"]*)\"(?: on module \"([^\"]*)\")? with empty X-Auth-UserId header$")
    public void getRequestIsSentToOnModuleWithEmptyXAuthUserIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlByUser(null, url, module);
    }

    @When("^DELETE request is sent to \"([^\"]*)\" on module \"([^\"]*)\"$")
    public void deleteRequestIsSentToOnModule(String url, String module) throws Throwable {
        basicSteps.sendDeleteToUrl(url, module);
    }

    @When("^GET request is sent to \"([^\"]*)\"(?: on module \"([^\"]*)\")? without X-Auth-AppId header$")
    public void getRequestIsSentToOnModuleWithoutXAuthAppIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlWithoutAppHeader(url, module);
    }

    @When("^GET request is sent to \"([^\"]*)\"(?: on module \"([^\"]*)\")? with empty X-Auth-AppId header$")
    public void getRequestIsSentToOnModuleWithEmptyXAuthAppIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlByUserForApp(DEFAULT_SNAPSHOT_USER_ID, null, url, module);
    }
}