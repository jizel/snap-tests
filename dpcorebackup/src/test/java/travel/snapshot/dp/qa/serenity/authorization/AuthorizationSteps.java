package travel.snapshot.dp.qa.serenity.authorization;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import lombok.extern.java.Log;
import net.serenitybdd.core.Serenity;

import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

@Log
public class AuthorizationSteps extends BasicSteps {

    public AuthorizationSteps() {
        super();
    }

    public void getConfigurationData(String url, String access_token) {
        RequestSpecification requestSpecification = given()
                .baseUri(propertiesHelper.getProperty(CONFIGURATION_NGINX_BASE_URI))
                .parameter("access_token", access_token);

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void getConfigurationDataWithNewToken(String url, String username, String password) {
        String access_token = given()
                .baseUri(propertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
                .parameter("client_id", "fad6b992")
                .parameter("client_secret", "133707cc5837af1b6a87a1dbb117b978")
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password)
                .parameter("code", "abcde")
                .get("/oauth/token")
                .path("access_token");
        System.out.println("token = " + access_token);

        getConfigurationData(url, access_token);
    }

    public void getIdentityData(String url, String access_token) {
        RequestSpecification requestSpecification = given()
                .baseUri(propertiesHelper.getProperty(IDENTITY_NGINX_BASE_URI))
                .parameter("access_token", access_token);

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void getIdentityDataWithNewToken(String url, String username, String password) {
        String access_token = given()
                .baseUri(propertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
                .parameter("client_id", "fad6b992")
                .parameter("client_secret", "133707cc5837af1b6a87a1dbb117b978")
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password)
                .parameter("code", "abcde")
                .get("/oauth/token")
                .path("access_token");

        getIdentityData(url, access_token);
    }

    public Response getToken(String username, String password, String clientId, String clientSecret) {
        RequestSpecification requestSpecification = given()
                .baseUri(propertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
                .parameter("client_id", clientId)
                .parameter("client_secret", clientSecret)
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password)
                .relaxedHTTPSValidation()
                .log().all();
        Response response = requestSpecification.post("/oauth/token");
        setSessionResponse(response);
        if (response.getStatusCode() == SC_OK) {
            String token = response.path("access_token");
            Serenity.setSessionVariable(SESSION_TOKEN).to(token);
        } else {
            log.warning("Failed to receive oauth token");
        }
        return response;
    }
}
