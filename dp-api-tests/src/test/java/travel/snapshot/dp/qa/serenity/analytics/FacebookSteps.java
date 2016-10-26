package travel.snapshot.dp.qa.serenity.analytics;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class FacebookSteps extends AnalyticsBaseSteps {


    public FacebookSteps() {
        spec.baseUri(PropertiesHelper.getProperty(FACEBOOK_BASE_URI));
    }

    /**
     * Method is design that it will check only first facebook post for correct data, for moving to
     * another ones you can use cursor in request
     * - Can we do this differently? Refactor.
     */
    @Step
    public void checkFacebookPostFromResponse(String datetimeExpected, int engagementExpected, String contentExpected, int reachExpected) throws Exception {
        Response response = getSessionResponse();
            assertThat("Datetime in response is wrong", response.jsonPath().getList("datetime").get(0), equalTo(datetimeExpected));
            assertThat("Engagement in response is wrong", response.jsonPath().getList("engagement").get(0), equalTo(engagementExpected));
            assertThat("Content in response is wrong", response.jsonPath().getList("content").get(0), equalTo(contentExpected));
            assertThat("Reach in response is wrong", response.jsonPath().getList("reach").get(0), equalTo(reachExpected));
    }
}
