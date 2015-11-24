package travel.snapshot.dp.qa.serenity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class BasicSteps {

    protected static final String SESSION_RESPONSE = "response";
    protected static final String SESSION_RESPONSE_MAP = "response_map";
    protected static final String SOCIAL_MEDIA_BASE_URI = "social_media.baseURI";
    protected static final String IDENTITY_BASE_URI = "identity.baseURI";
    protected static final String CONFIGURATION_BASE_URI = "configuration.baseURI";
    protected static final String SECOND_LEVEL_OBJECT_PROPERTIES = "properties";
    protected static final String LIMIT_TO_ALL = "1000000";
    protected static final String LIMIT_TO_ONE = "1";
    protected static final String CURSOR_FROM_FIRST = "0";
    protected static final String SECOND_LEVEL_OBJECT_USERS = "users";
    protected static final String SECOND_LEVEL_OBJECT_PROPERTY_SETS = "property_sets";
    private static final String CONFIGURATION_REQUEST_HTTP_LOG_LEVEL = "http_request_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL = "http_response_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_STATUS = "http_response_log_status";
    protected static final String HEADER_ETAG = "ETag";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";

    protected RequestSpecification spec = null;

    public BasicSteps() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        String responseLogLevel = PropertiesHelper.getProperty(CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL);
        String requestLogLevel = PropertiesHelper.getProperty(CONFIGURATION_REQUEST_HTTP_LOG_LEVEL);

        if (StringUtils.isNotBlank(responseLogLevel)) {
            builder.log(LogDetail.valueOf(requestLogLevel));
        }

        if (StringUtils.isNotBlank(responseLogLevel)) {
            RestAssured.filters(
                    new ResponseLoggingFilter(
                            LogDetail.valueOf(responseLogLevel),
                            true,
                            System.out,
                            not(isOneOf(PropertiesHelper.getListOfInt(CONFIGURATION_RESPONSE_HTTP_LOG_STATUS)))));
        }

        builder.setContentType(ContentType.JSON);
        spec = builder.build();
    }

    private String getRequestDataFromFile(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Charset.forName("utf-8"));
    }

    public void responseCodeIs(int responseCode) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().statusCode(responseCode);
    }

    public void contentTypeIs(String contentType) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().contentType(contentType);
    }

    public void customCodeIs(Integer customCode) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("code", is(customCode));
    }

    public void bodyIsEmpty() {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(isEmptyOrNullString());
    }

    @Step
    public void bodyContainsEntityWith(String attributeName, String attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(attributeValue));
    }

    public void bodyContainsEntityWith(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, notNullValue());
    }

    public void bodyDoesntContainEntityWith(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, nullValue());
    }

    public void isCalledWithoutTokenUsingMethod(String service, String method) {
        Response response = spec
                .when()
                .get(service);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void etagIsPresent() {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header(HEADER_ETAG, not(isEmptyOrNullString()));
    }

    public void useFileForSendDataTo(String filename, String method, String url, String module) throws Exception {
        if (!"POST".equals(method)) {
            throw new Exception("Cannot use this method for other methods than POST");
        }
        switch (module) {
            case "identity": {
                spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
                break;
            }
            case "configuration": {
                spec.baseUri(PropertiesHelper.getProperty(CONFIGURATION_BASE_URI));
                break;
            }
            case "social_media": {
                spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
                break;
            }
            default:
        }
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Response response = given().spec(spec).basePath(url)
                .body(data)
                .when().post();
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session

    }

    protected Response getEntity(String id) {
        return getEntity(id, null);
    }

    protected Response createEntity(Object entity) {
        return given().spec(spec).body(entity).when().post();
    }

    protected Response updateEntity(String id, Map<String, Object> object, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(object).when().post("/{id}", id);
    }

    protected Response deleteEntity(String id) {
        return given().spec(spec).when().delete("/{id}", id);
    }

    protected Response getEntity(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        return requestSpecification.when().get("/{id}", id);
    }

    protected Response getSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        return requestSpecification.when().get("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response deleteSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId) {
        return given().spec(spec)
                .when().delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response updateSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, Map<String, Object> object, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(object).when().post("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    /**
     * getting entities over rest api, if limit and cursor is null or empty, it's not added to query
     * string
     *
     * @param sortDesc @return
     */
    protected Response getEntities(String limit, String cursor, String filter, String sort, String sortDesc) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        if (filter != null) {
            requestSpecification.parameter("filter", filter);
        }
        if (sort != null) {
            requestSpecification.parameter("sort", sort);
        }
        if (sortDesc != null) {
            requestSpecification.parameter("sort_desc", sortDesc);
        }

        return requestSpecification.when().get();
    }

    protected Response getSecondLevelEntities(String firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        if (filter != null) {
            requestSpecification.parameter("filter", filter);
        }
        if (sort != null) {
            requestSpecification.parameter("sort", sort);
        }
        if (sortDesc != null) {
            requestSpecification.parameter("sort_desc", sortDesc);
        }

        return requestSpecification.when().get("{id}/{secondLevelName}", firstLevelId, secondLevelObjectName);
    }


    protected <T> Map<String, Object> retrieveData(Class<T> c, T entity) throws IntrospectionException, ReflectiveOperationException {
        Map<String, Object> data = new HashMap<>();
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
            Method getter = descriptor.getReadMethod();
            Object value = getter.invoke(entity);
            if (value != null) {
                JsonProperty jsonProperty = getter.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !value.toString().equals("/null")) {
                    data.put(getter.getAnnotation(JsonProperty.class).value(), value);
                }
            }
        }
        return data;
    }

    // --- session access ---

    protected void setSessionResponse(Response response) {
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    protected Response getSessionResponse() {
        return Serenity.<Response>sessionVariableCalled(SESSION_RESPONSE);
    }

    public void setSessionResponseMap(Map<String, Response> responses) {
        Serenity.setSessionVariable(SESSION_RESPONSE_MAP).to(responses);
    }

    public Map<String, Response> getSessionResponseMap() {
        return Serenity.<Map<String, Response>>sessionVariableCalled(SESSION_RESPONSE_MAP);
    }

    protected void setSessionVariable(String key, Object value) {
        Serenity.setSessionVariable(key).to(value);
    }

    protected <T> T getSessionVariable(String key) {
        return Serenity.<T>sessionVariableCalled(key);
    }

    public <T> void numberOfEntitiesInResponse(Class<T> clazz, int count) throws Throwable {
        Response response = getSessionResponse();
        ObjectMapper mapper = new ObjectMapper();
        List<T> objects = mapper.readValue(response.asString(), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        assertEquals("There should be " + count + " entities got", count, objects.size());
    }

    public void headerIs(String headerName, String value) {
        Response response = getSessionResponse();
        response.then().header(headerName, is(value));
    }
}
