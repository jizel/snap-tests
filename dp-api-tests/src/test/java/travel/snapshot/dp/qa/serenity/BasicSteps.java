package travel.snapshot.dp.qa.serenity;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.helpers.ObjectMappers.OBJECT_MAPPER;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.qa.helpers.NullStringObjectValueConverter;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class BasicSteps {

    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String OAUTH_PARAMETER_NAME = "access_token";
    public static final String HEADER_XAUTH_USER_ID = "X-Auth-UserId";
    public static final String DEFAULT_SNAPSHOT_USER_ID = "11111111-0000-4000-a000-000000000000";
    public static final String NON_EXISTENT_ID = "00000000-0000-4000-a000-000000000000";
    protected static final String SESSION_RESPONSE = "response";
    protected static final String SESSION_RESPONSE_MAP = "response_map";
    protected static final String SOCIAL_MEDIA_BASE_URI = "social_media.baseURI";
    protected static final String INSTAGRAM_BASE_URI = "instagram.baseURI";
    protected static final String FACEBOOK_BASE_URI = "facebook.baseURI";
    protected static final String REVIEW_BASE_URI = "review.baseURI";
    protected static final String TWITTER_BASE_URI = "twitter.baseURI";
    protected static final String WEB_PERFORMANCE_BASE_URI = "web_performance.baseURI";
    protected static final String RATE_SHOPPER_BASE_URI = "rate_shopper.baseURI";
    protected static final String IDENTITY_BASE_URI = "identity.baseURI";
    protected static final String IDENTITY_NGINX_BASE_URI = "identity_nginx.baseURI";
    protected static final String CONFIGURATION_BASE_URI = "configuration.baseURI";
    protected static final String CONFIGURATION_NGINX_BASE_URI = "configuration_nginx.baseURI";
    protected static final String LIMIT_TO_ALL = "200";
    protected static final String LIMIT_TO_ONE = "1";
    protected static final String CURSOR_FROM_FIRST = "0";
    protected static final String SECOND_LEVEL_OBJECT_PROPERTIES = "properties";
    protected static final String SECOND_LEVEL_OBJECT_USERS = "users";
    protected static final String SECOND_LEVEL_OBJECT_PROPERTY_SETS = "property_sets";
    protected static final String SECOND_LEVEL_OBJECT_CUSTOMERS = "customers";
    protected static final String SECOND_LEVEL_OBJECT_RECORDS = "records";
    protected static final String SECOND_LEVEL_OBJECT_ROLES = "roles";
    protected static final String SECOND_LEVEL_OBJECT_VERSIONS = "application_versions";
    protected static final String SECOND_LEVEL_OBJECT_API_SUBSCRIPTION = "api_subscriptions";
    protected static final String AUTHORIZATION_BASE_URI = "authorization.baseURI";
    protected static final String HEADER_ETAG = "ETag";
    protected static final String SECOND_LEVEL_OBJECT_APPLICATIONS = "applications";
    protected static final String SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS = "commercial_subscriptions";
    protected static final String CURLY_BRACES_EMPTY = "{}";
    private static final String CONFIGURATION_REQUEST_HTTP_LOG_LEVEL = "http_request_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL = "http_response_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_STATUS = "http_response_log_status";
    protected RequestSpecification spec = null;

    public BasicSteps() {

        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> OBJECT_MAPPER));

        RequestSpecBuilder builder = new RequestSpecBuilder();

        String responseLogLevel = PropertiesHelper.getProperty(CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL);
        String requestLogLevel = PropertiesHelper.getProperty(CONFIGURATION_REQUEST_HTTP_LOG_LEVEL);

        if (isNotBlank(responseLogLevel)) {
            builder.log(LogDetail.valueOf(requestLogLevel));
        }

        if (isNotBlank(responseLogLevel)) {
            RestAssured.replaceFiltersWith(
                    new ResponseLoggingFilter(
                            LogDetail.valueOf(responseLogLevel),
                            true,
                            System.out,
                            not(isOneOf(PropertiesHelper.getListOfInt(CONFIGURATION_RESPONSE_HTTP_LOG_STATUS)))));
        }

        builder.setContentType("application/json; charset=UTF-8");
        spec = builder.build();
    }


    public void setAccessTokenParamFromSession() {
        String token = getSessionVariable(OAUTH_PARAMETER_NAME);
        if (isNotEmpty(token)) {
            spec.queryParam(OAUTH_PARAMETER_NAME, token);
        }
    }

    public String getRequestDataFromFile(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Charset.forName("utf-8"));
    }

    public void responseCodeIs(int responseCode) {
        Response response = getSessionResponse();
        response.then().statusCode(responseCode);
    }

    public void contentTypeIs(String contentType) {
        Response response = getSessionResponse();
        response.then().contentType(contentType);
    }

    public void customCodeIs(Integer customCode) {
        Response response = getSessionResponse();
        response.then().body("code", is(customCode));
    }

    public void bodyIsEmpty() {
        Response response = getSessionResponse();
        response.then().body(isEmptyOrNullString());
    }

    @Step
    public void bodyContainsCollectionWith(String attributeName, Object item) {
        Response response = getSessionResponse();
        response.then().body(attributeName, hasItem(item));
    }

    /**
     * This method is used instead of bodyContainsCollectionWith() when the collection contains
     * values of type Double. Only the integer part of the value is validated.
     */
    public void integerPartOfValueIs(String path, int value) {
        Response response = getSessionResponse();
        response.then().body(path, hasItem(value));
    }

    @Step
    public void bodyContainsR(String attributeName, BigDecimal item) {
        Response response = getSessionResponse();
        response.then().body(attributeName, hasItem(item));
    }

    @Step
    public void bodyContainsEntityWith(String attributeName, String attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, isOneOf(attributeValue, Boolean.valueOf(attributeValue)));
    }

    @Step
    public void bodyContainsEntityWith(String attributeName, Integer attributeValue) {
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
        setSessionResponse(response);
    }

    public void etagIsPresent() {
        Response response = getSessionResponse();
        response.then().header(HEADER_ETAG, not(isEmptyOrNullString()));
    }

    public void useFileForSendDataTo(String filename, String method, String url, String module) throws Exception {
        if (!"POST".equals(method)) {
            throw new Exception("Cannot use this method for other methods than POST");
        }
        setBaseUriForModule(module);
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Response response = given().spec(spec).basePath(url).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .body(data)
                .when().post();
        setSessionResponse(response);

    }

    public String getAttributeValue(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, notNullValue());

        return response.getBody().jsonPath().get(attributeName).toString();
    }

    protected void setBaseUriForModule(String module) {
        switch (module) {
            case "identity": {
                spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
                break;
            }
            case "configurations": {
                spec.baseUri(PropertiesHelper.getProperty(CONFIGURATION_BASE_URI));
                break;
            }
            case "social_media": {
                spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
                break;
            }
            case "authorization": {
                spec.baseUri(PropertiesHelper.getProperty(AUTHORIZATION_BASE_URI));
                break;
            }
            default:
        }
    }

    protected Response getEntity(String id) {
        return getEntity(id, null);
    }

    protected Response getEntityByUser(String userId, String entityId) {
        return getEntityByUser(userId, entityId, null);
    }

    protected Response createEntity(Object entity) {
        return createEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entity);
    }

    protected Response createEntityByUser(String userId, Object entity) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).body(entity).when().post();
    }

    protected Response updateEntity(String entityId, Map<String, Object> data, String etag) {
        return updateEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, data, etag);
    }

    protected Response updateEntityByUser(String userId, String entityId, Map<String, Object> data, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)){
            fail("User ID to be send in request header is null.");
        }
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        return requestSpecification.body(data).when().post("/{id}", entityId);
    }

    protected Response updateEntity(String entityId, String data, String etag) {
        return updateEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, data, etag);
    }

    protected Response updateEntityByUser(String userId, String entityId, String data, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)){
            fail("User ID to be send in request header is null.");
        }
        if (isNotBlank(etag)) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        return requestSpecification.body(data).when().post("/{id}", entityId);
    }

    protected Response updateEntityWithEtag(String entityId, String data) {
        return updateEntityWithEtagByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, data);
    }

    protected Response updateEntityWithEtagByUser(String userId, String entityId, String data) {
        String etag = getEntity(entityId, null).getHeader(HEADER_ETAG);
        return updateEntityByUser(userId, entityId, data, etag);
    }


    protected Response deleteEntity(String entityId, String etag) {
        return deleteEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, etag);
    }

    protected Response deleteEntityByUser(String userId, String entityId, String etag) {
        if (isBlank(userId)){
            fail("User ID to be send in request header is blank.");
        }
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        if (isNotBlank(etag)) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.when().delete("/{id}", entityId);
    }

    protected void deleteEntityWithEtag(String entityId) {
        String etag = getEntity(entityId).getHeader(HEADER_ETAG);
        Response response = deleteEntity(entityId, etag);
        setSessionResponse(response);
    }

    protected void deleteEntityWithEtagByUser(String userId, String entityId) {
        String etag = getEntity(entityId).getHeader(HEADER_ETAG);
        Response response = deleteEntityByUser(userId, entityId, etag);
        setSessionResponse(response);
    }

    public Response deleteEntityUrl(String url, String id) {
        return given().spec(spec).when().delete(url + "/{id}", id);
    }

    protected Response getEntity(String entityId, String etag) {
        return getEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, etag);
    }

    protected Response getEntityByUser(String userId, String entityId, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)){
            fail("User ID to be send in request header is null.");
        }
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        return requestSpecification.when().get("/{id}", entityId);
    }

    protected Response createSecondLevelRelationship(String firstLevelId, String secondLevelId, Object jsonBody) {
        return createSecondLevelRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelId, jsonBody);
    }

    protected Response createSecondLevelRelationshipByUser(String userId, String firstLevelId, String secondLevelId, Object jsonBody) {
        RequestSpecification requestSpecification = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).body(jsonBody);
        return requestSpecification.post("/" + firstLevelId + "/" + secondLevelId);
    }

    protected Response getSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, String etag) {
        return getSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, etag);
    }
    protected Response getSecondLevelEntityByUser(String userId, String firstLevelId, String secondLevelObjectName, String secondLevelId, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        if (isNotBlank(userId)) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        return requestSpecification.when().get("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response deleteSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, Map<String, String> queryParams) {
        return deleteSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, queryParams);
    }

    protected Response deleteSecondLevelEntityByUser(String userId, String firstLevelId, String secondLevelObjectName, String secondLevelId, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        String etag = getSecondLevelEntity(firstLevelId, secondLevelObjectName, secondLevelId, null).getHeader(HEADER_ETAG);
        if (isNotBlank(etag)) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        if (isNotBlank(userId)) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        if (queryParams != null) {
            requestSpecification.parameters(queryParams);
        }
        return requestSpecification
                .when().delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response deleteSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId) {
        return deleteSecondLevelEntity(firstLevelId, secondLevelObjectName, secondLevelId, null);
    }

    protected Response updateSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, Map<String, Object> object, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(object).when().post("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response updateSecondLevelEntity(String firstLevelId, String secondLevelObjectName, String secondLevelId, JSONObject object, String etag) {
        return updateSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, object, etag);
    }
    protected Response updateSecondLevelEntityByUser(String userId, String firstLevelId, String secondLevelObjectName, String secondLevelId, JSONObject object, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        if(isNotBlank(userId)){
            requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        return requestSpecification.body(object.toString()).when().post("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    @Step
    public void sendBlankPost(String url, String module){
        sendPostWithBody(url,module,"");
    }

    @Step
    public void sendPostWithBody(String url, String module, String body){
        setBaseUriForModule(module);
        Response response = given().spec(spec).basePath(url).body(body).when().post();

//        If request needs ETag header (for updates). I know this looks awful and it makes a few redundant api calls but other solutions involve needles meta-information in gherkin scenario (boolean needsETag or something like that).
        if(response.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED){
            RequestSpecification requestSpecification = given().spec(spec).basePath(url);
            String etag = requestSpecification.header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).when().get().getHeader(HEADER_ETAG);
            assertThat("ETag was not obtained", etag, not(isEmptyOrNullString()));
            requestSpecification.header(HEADER_IF_MATCH, etag);

            response = requestSpecification.body(body).when().post();
        }

        setSessionResponse(response);
    }

    @Step
    public void sendGetRequestToUrl(String url, String module) {
       sendGetRequestToUrlByUser(DEFAULT_SNAPSHOT_USER_ID, url, module);
    }

    @Step
    public void sendGetRequestToUrlByUser(String userId, String url, String module) {
        setBaseUriForModule(module);
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).basePath(url).when().get();
        setSessionResponse(response);
    }

    @Step
    public void sendGetRequestToUrlWithoutUserHeader(String url, String module) {
        setBaseUriForModule(module);
        Response response = given().spec(spec).basePath(url).when().get();
        setSessionResponse(response);
    }


    /**
     * getting entities over rest api, if limit and cursor is null or empty, it's not added to query
     * string
     *
     * @param sortDesc @return
     */
    protected Response getEntities(String limit, String cursor, String filter, String sort, String sortDesc) {
        return getEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, null, limit, cursor, filter, sort, sortDesc, null);
    }

    protected Response getEntities(String url, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, url, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getEntitiesByUser(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return getEntitiesByUser(userId, null, limit, cursor, filter, sort, sortDesc, null);
    }

    protected Response getEntitiesByUser(String userId, String url, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)){
            fail("User ID to be send in request header is null.");
        }
        if (url == null) {
            url = "";
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);
        requestSpecification.parameters(params);

        return requestSpecification.when().get(url);
    }

    protected Response getEntitiesForUrlWihDates(String url, String limit, String cursor, String since, String until, String granularity, Map<String, String> queryParams) {
        Map<String, String> preparedParams = buildQueryParamMapForDates(since, until, granularity);
        if (queryParams != null) {
            preparedParams.putAll(queryParams);
        }
        return getEntities(url, limit, cursor, null, null, null, preparedParams);
    }

    /**
     * Method will ignore parsing of date if it is incorrectly hand out to method for purpose of
     * negative tests
     *
     * @param since       correct format 2015-01-01 / today / today -1 month
     * @param until       same as "since"
     * @param granularity day/week/month
     * @return Map of query parameters that is passed to getEntitiesForUrlWihDates
     */
    private Map<String, String> buildQueryParamMapForDates(String since, String until, String granularity) {
        Map<String, String> queryParams = new HashMap<>();
        try {
            LocalDate sinceDate = StringUtil.parseDate(since);
            if (sinceDate != null) {
                queryParams.put("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
            }
        } catch (DateTimeParseException e) {
            queryParams.put("since", since);
        }

        try {
            LocalDate untilDate = StringUtil.parseDate(until);
            if (untilDate != null) {
                queryParams.put("until", untilDate.format(DateTimeFormatter.ISO_DATE));
            }
        } catch (DateTimeParseException e) {
            queryParams.put("until", until);
        }

        if (isNotBlank(granularity)) {
            queryParams.put("granularity", granularity);
        }
        return queryParams;
    }

    private Map<String, String> buildQueryParamMapForPaging(String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParameters) {
        Map<String, String> queryParams = new HashMap<>();
        if (cursor != null) {
            queryParams.put("cursor", cursor);
        }
        if (limit != null) {
            queryParams.put("limit", limit);
        }
        if (filter != null) {
            queryParams.put("filter", filter);
        }
        if (sort != null) {
            queryParams.put("sort", sort);
        }
        if (sortDesc != null) {
            queryParams.put("sort_desc", sortDesc);
        }
        if (queryParameters != null) {
            queryParams.putAll(queryParameters);
        }

        return queryParams;
    }

    protected Response getSecondLevelEntities(String firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getSecondLevelEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getSecondLevelEntities(String firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc) {
        return getSecondLevelEntities(firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, null);
    }

    protected Response getSecondLevelEntitiesByUser(String userId, String firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)){
            fail("User ID to be send in request header is null.");
        }
        requestSpecification.header(HEADER_XAUTH_USER_ID, userId);

        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);
        requestSpecification.parameters(params);

        return requestSpecification.when().get("{id}/{secondLevelName}", firstLevelId, secondLevelObjectName);
    }

    protected Response getSecondLevelEntitiesForDates(String firstLevelId, String secondLevelObjectName, String limit, String cursor, String since, String until, String granularity, String filter, String sort, String sortDesc) {
        Map<String, String> queryParams = buildQueryParamMapForDates(since, until, granularity);

        return getSecondLevelEntities(firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected JSONObject retrieveData(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String customerData = mapper.writeValueAsString(value);
        return NullStringObjectValueConverter.transform(customerData);
    }

    protected <T> Map<String, Object> retrieveDataOld(Class<T> c, T entity) throws IntrospectionException, ReflectiveOperationException {
        Map<String, Object> data = new HashMap<>();
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
            Method getter = descriptor.getReadMethod();
            Enumeration<String> atts = descriptor.attributeNames();
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

    public Response getSessionResponse() {
        return Serenity.<Response>sessionVariableCalled(SESSION_RESPONSE);
    }

    public void setSessionResponse(Response response) {
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public Map<String, Response> getSessionResponseMap() {
        return Serenity.<Map<String, Response>>sessionVariableCalled(SESSION_RESPONSE_MAP);
    }

    public void setSessionResponseMap(Map<String, Response> responses) {
        Serenity.setSessionVariable(SESSION_RESPONSE_MAP).to(responses);
    }

    public void setSessionVariable(String key, Object value) {
        Serenity.setSessionVariable(key).to(value);
    }

    public <T> T getSessionVariable(String key) {
        return Serenity.<T>sessionVariableCalled(key);
    }

    @Step
    public <T> void numberOfEntitiesInResponse(Class<T> clazz, int count) throws Throwable {
        Response response = getSessionResponse();
        List<T> objects = OBJECT_MAPPER.readValue(response.asString(), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        assertEquals("There should be " + count + " entities got", count, objects.size());
    }

    public void headerIs(String headerName, String value) {
        Response response = getSessionResponse();
        response.then().header(headerName, is(value));
    }

    public void responseContainsNoOfAttributes(int count, String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName + ".size()", is(count));
    }
}
