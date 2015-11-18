package travel.snapshot.dp.qa.serenity.authorization;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.Stats;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthorizationSteps extends BasicSteps {


    public AuthorizationSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(AUTHORIZATION_BASE_URI));
    }
    
    public void postData(String url, String username, String password) {
        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password);

        Response response = requestSpecification.when().post(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }   
}
