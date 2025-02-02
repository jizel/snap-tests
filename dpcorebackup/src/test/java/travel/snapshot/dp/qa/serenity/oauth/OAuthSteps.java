package travel.snapshot.dp.qa.serenity.oauth;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.Map;

import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by vlcek on 2/9/2016.
 */
public class OAuthSteps extends BasicSteps {

    private static final String BASE_PATH__OAUTH = "/oauth/authorize";
    private static final String OAUTH_CODE = "s24fet6yhd7";

    public OAuthSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__OAUTH);
    }

    @Step
    public void getToken(String username, String password) {
        Response response = getTokenResponse(username, password);
        setSessionResponse(response);
        setSessionVariable(OAUTH_PARAMETER_NAME, response.getBody().asString());
    }

    private Response getTokenResponse(String username, String password) {
        // Prepare query parameters
        Map<String, String> queryParams = new HashMap<String, String>() {{
            put("username", username);
            put("password", password);
            put("code", OAUTH_CODE);
        }};
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification.parameters(queryParams);
        return requestSpecification.get();
    }

}
