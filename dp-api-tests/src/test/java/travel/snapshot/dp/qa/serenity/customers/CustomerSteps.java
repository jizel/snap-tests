package travel.snapshot.dp.qa.serenity.customers;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.AddressUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class CustomerSteps extends BasicSteps {

    private static final String SESSION_CUSTOMERS = "customers";
    private static final String SESSION_CREATED_CUSTOMER = "created_customer";
    private static final String SESSION_CUSTOMER_ID = "customer_id";
    private static final String SESSION_CREATED_CUSTOMER_PROPERTY = "created_customer_property";
    private static final String BASE_PATH_CUSTOMERS = "/identity/customers";
    private static final String DEFAULT_VALID_FROM = "2015-12-31";
    private static final String DEFAULT_VALID_TO = "2050-12-31";
    private static final String DEFAULT_RELATION_TYPE = "chain";

    public CustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMERS);
    }

    @Step
    public void followingCustomersExistWithRandomAddress(List<CustomerCreateDto> customers) {
        customers.forEach(t -> {
            if (t.getType() == null) {
                t.setType(CustomerType.valueOf(DEFAULT_CUSTOMER_TYPE));
            }
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Customer cannot be created " + createResponse.getBody().asString());
            }
            setSessionResponse(createResponse);
        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void followingCustomersExist(List<CustomerCreateDto> customers) {
        customers.forEach(customer -> {
            Response createResponse = createEntity(customer);
            assertThat("Customer cannot be created " + createResponse.getBody().asString(), createResponse.getStatusCode(), is(HttpStatus.SC_CREATED));
            setSessionResponse(createResponse);
        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void fileIsUsedForCreation(String filename) {
        CustomerDto customer = getCustomerFromFile(this.getClass().getResourceAsStream(filename));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreatedWithRandomAddress(CustomerCreateDto customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreated(CustomerCreateDto customer) {
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreatedWithAddress(CustomerCreateDto customer, AddressDto address) {
        customer.setAddress(address);
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void compareCustomerPropertyOnHeaderWithStored(String headerName) {
        CustomerPropertyRelationshipDto originalCustomerProperty = getSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(BASE_PATH_CUSTOMERS, "");
        given().spec(spec).get(customerLocation).then()
                .body("property_id", is(originalCustomerProperty.getId()))
                //.body("type", is(originalCustomerProperty.getType()))
                .body("valid_from", is(originalCustomerProperty.getValidFrom().format( DateTimeFormatter.ISO_LOCAL_DATE )))
                .body("valid_to", is(originalCustomerProperty.getValidTo().format( DateTimeFormatter.ISO_LOCAL_DATE )));

    }

    private CustomerDto getCustomerFromFile(InputStream inputStream) {
        return from(inputStream).getObject("", CustomerDto.class);
    }

    private CustomerPropertyRelationshipDto getCustomerPropertyForCustomer(String customerId, String propertyId) {
        String filter = String.format("property_id==%s", propertyId);
        CustomerPropertyRelationshipDto[] customerProperties = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(CustomerPropertyRelationshipDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    public CustomerPropertyRelationshipDto getCustomerPropertyRelationship(String customerId, String propertyId) {
        return getCustomerPropertyRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, customerId, propertyId);
    }

    public CustomerPropertyRelationshipDto getCustomerPropertyRelationshipByUser(String userId, String customerId, String propertyId) {
        String filter = String.format("property_id==%s", propertyId);
        CustomerPropertyRelationshipDto[] customerProperties = getSecondLevelEntitiesByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, null, null, filter, null, null, null).as(CustomerPropertyRelationshipDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    @Step
    private Response addPropertyToCustomer(String propertyId, String customerId, String type, String validFrom, String validTo, Boolean isActive) {
        return addPropertyToCustomerByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, customerId, type, validFrom, validTo, isActive);
    }

    @Step
    public Response addPropertyToCustomerByUser(String userId, String propertyId, String customerId, String type, String validFrom, String validTo, Boolean isActive) {
        return addPropertyToCustomerByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, customerId, type, validFrom, validTo, isActive);
    }

    @Step
    public Response addPropertyToCustomerByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId, String type, String validFrom, String validTo, Boolean isActive) {
        CustomerPropertyRelationshipDto relation = new CustomerPropertyRelationshipDto();
        if (userId == null) {
            userId = DEFAULT_SNAPSHOT_USER_ID;
        }
        if (propertyId == null) {
            propertyId = NON_EXISTENT_ID;
        }
        if (type == null) {
            type = DEFAULT_RELATION_TYPE;
        }
        if (validFrom == null) {
            validFrom = DEFAULT_VALID_FROM;
        }
        if (validTo == null) {
            validTo = DEFAULT_VALID_TO;
        }
        relation.setType(CustomerPropertyRelationshipType.valueOf(type.toUpperCase()));
        relation.setValidFrom(LocalDate.parse(validFrom));
        relation.setValidTo(LocalDate.parse(validTo));
        relation.setIsActive(isActive);
        relation.setPropertyId(propertyId);
        Response response =  createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, relation);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addPropertyToCustomerByUserForAppInvalid(String userId, String applicationVersionId, String propertyId, String customerId, String type, String validFrom, String validTo, Boolean isActive) {
        Map<String, String> customerPropertyRelationMap = new HashMap<>();
        customerPropertyRelationMap.put(RELATIONSHIP_TYPE, type.toUpperCase());
        customerPropertyRelationMap.put(VALID_FROM, validFrom);
        customerPropertyRelationMap.put(VALID_TO, validTo);
        customerPropertyRelationMap.put(IS_ACTIVE, isActive.toString());
        customerPropertyRelationMap.put(PROPERTY_ID, propertyId);
        Response response =  createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, customerPropertyRelationMap);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addUserToCustomer(String userId, String customerId, Boolean isPrimary, Boolean isActive) {
        return addUserToCustomerByUser(DEFAULT_SNAPSHOT_USER_ID, userId, customerId, isPrimary, isActive);
    }

    @Step
    public Response addUserToCustomerByUser(String performerId, String userId, String customerId, Boolean isPrimary, Boolean isActive) {
        Response response = addUserToCustomerByUserForApp(performerId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, customerId, isPrimary, isActive);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addUserToCustomerByUserForApp(String performerId, String applicationVersionId, String userId, String customerId, Boolean isPrimary, Boolean isActive) {
        CustomerUserRelationshipDto relation = new CustomerUserRelationshipDto();
        relation.setUserId(userId);
        relation.setIsPrimary(isPrimary);
        relation.setIsActive(isActive);
        Response response = createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_USERS, relation);
        setSessionResponse(response);
        return response;
    }


    public CustomerDto getCustomerById(String id) {
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + id, null, null, null).as(CustomerDto[].class);
        return stream(customers).findFirst().orElse(null);
    }

    public CustomerDto getCustomerByIdByUser(String customerId, String userId) {
        Response response = getEntityByUser(userId, customerId);
        CustomerDto customer = response.as(CustomerDto.class);
        setSessionResponse(response);
        return customer;
    }

    @Step
    public void customerWithIdIsGot(String customerId) {
        customerWithIdIsGotByUser(DEFAULT_SNAPSHOT_USER_ID, customerId);
    }

    @Step
    public void customerWithIdIsGotByUser(String userId, String customerId) {
        customerWithIdIsGotByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customerId);
    }

    @Step
    public void customerWithIdIsGotByUserForApplication(String userId, String applicationId, String customerId) {
        Response response = getEntityByUserForApplication(userId, applicationId, customerId);
        setSessionResponse(response);
    }

    @Step
    public void deleteCustomer(String customerId) {
        deleteEntityWithEtag(customerId);
    }

    @Step
    public void deleteCustomerByUser(String userId, String customerId) {
        deleteEntityWithEtagByUser(userId, customerId);
    }

    @Step
    public void deleteCustomerByUserForApp(String userId, String applicationVersionId, String customerId) {
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, customerId);
    }

    @Step
    public Response listOfCustomersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfCustomersIsGotByUserWith(DEFAULT_SNAPSHOT_USER_ID, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfCustomersIsGotByUserWith(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfCustomersIsGotByUserForAppVersionWith(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfCustomersIsGotByUserForAppVersionWith(String userId, String applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void updateCustomer(String customerId, CustomerUpdateDto updatedCustomer) {
        updateCustomerByUser(customerId, DEFAULT_SNAPSHOT_USER_ID, updatedCustomer);
    }

    @Step
    public void updateCustomerByUser(String customerId, String userId, CustomerUpdateDto updatedCustomer) {
        updateCustomerByUserForApp(customerId, userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, updatedCustomer);
    }

    @Step
    public void updateCustomerByUserForApp(String customerId, String userId, String applicationVersionId, CustomerUpdateDto updatedCustomer) {
        try {
            String updatedCustomerString = retrieveData(updatedCustomer).toString();
            assertThat("Empty property update", updatedCustomerString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));

            String etag = getEntityEtag(customerId);
            Response response = updateEntityByUserForApplication(userId, applicationVersionId, customerId, updatedCustomerString, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException jsonException){
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public void updateCustomerAddress(String customerId, AddressUpdateDto updatedAddress) throws Throwable {
        Response temp = getEntity(customerId);
        if (temp.getStatusCode() != HttpStatus.SC_OK) {
            fail("Customer " + customerId + " not found!");
        }

        JSONObject addressJson = new JSONObject();
        JSONObject regexGenerated = RegexValueConverter.transform(retrieveData(updatedAddress));
        addressJson.putOpt("address", regexGenerated);

        Response response = updateEntity(customerId, addressJson.toString(), temp.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }


    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        //        Does not work - to be removed and replaced when testing DP-1319
        CustomerDto customer = getCustomerById(code);
        if (activeFlag) {
            assertNotNull("Customer should be returned", customer);
            assertEquals("Customer should have code=" + code, code, customer.getCode());
        } else {
//          Change isActive flag from 0 to true when running against DP version with DP-1319 merged
//           TODO: uncomment assertThat("Customer should have isActive flag set to 0", customer.getIsActive(), equalTo(0));
        }
    }

    @Step
    public void setCustomerIsActive(String customerId, Boolean isActive){
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setIsActive(isActive);
        updateCustomer(customerId, customerUpdate);
    }

    @Step
    public Boolean getCustomerIsActive(String customerId){
        return getCustomerById(customerId).getIsActive();
    }

    @Step
    public void customerWithIdIsGotWithEtag(String customerId) {
        Response resp = getEntity(customerId);
        setSessionResponse(resp);
    }

    @Step
    public void customerWithIdIsGotWithEtagByUser(String customerId, String userId) {
        Response resp = getEntity(customerId);
        setSessionResponse(resp);
    }


    @Step
    public void invalidCustomerUpdate(String customerId, Map<String, Object> updateMap) {
        Response response = updateEntity(customerId, updateMap, getEntity(customerId).getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    public void customerWithIdHasData(String customerId, String userId, CustomerDto data) throws Throwable {
        JSONObject customerFromDB = retrieveData(getCustomerByIdByUser(customerId, userId));
        JSONObject updatedData = retrieveData(data);

        Iterator<?> customerFromDBKeys = customerFromDB.keys();
        Iterator<?> updatedDataKeys = updatedData.keys();

        while (updatedDataKeys.hasNext()) {
            String key = (String) updatedDataKeys.next();

            Object updatedValue = updatedData.get(key);
            Object databaseValue = customerFromDB.get(key);

            assertEquals(updatedValue, databaseValue);
        }
    }

    @Step
    public void emailsAreInResponseInOrder(List<String> emails) {
        Response response = getSessionResponse();
        CustomerDto[] customers = response.as(CustomerDto[].class);
        int i = 0;
        for (CustomerDto c : customers) {
            contains("Customer on index=" + i + " is not expected", emails.get(i), c.getEmail());
            i++;
        }
    }

    @Step
    public void idsAreInResponseInOrder(List<String> ids) {
        Response response = getSessionResponse();
        PropertyCustomerRelationshipDto[] customers = response.as(PropertyCustomerRelationshipDto[].class);
        int i = 0;
        for (PropertyCustomerRelationshipDto customer : customers) {
            contains("Customer on index=" + i + " is not expected", ids.get(i), customer.getId());
            i++;
        }
    }

    @Step
    public void propertyIsAddedToCustomerFromTo(String propertyId, String customerId, String type, String dateFrom, String dateTo, Boolean isActive) {
        Response response = addPropertyToCustomer(propertyId, customerId, type, dateFrom, dateTo, isActive);

        if (response.statusCode() == HttpStatus.SC_CREATED) {
            setSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY, response.as(CustomerPropertyRelationshipDto.class));
        }
        setSessionResponse(response);
    }

    @Step
    public void relationExistsBetweenPropertyAndCustomerWithTypeFromTo(String propertyId, String customerId, String type, String validFrom, String validTo, Boolean isActive) {
        Response createResponse = addPropertyToCustomer(propertyId, customerId, type, validFrom, validTo, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerProperty cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public void propertyIsUpdateForCustomer(PropertyDto property, String customerId, String fieldName, String value) {
        CustomerPropertyRelationshipDto existingCustomerProperty = getCustomerPropertyForCustomer(customerId, property.getId());
        String etag = getSecondLevelEntityEtag(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getId());
        Response updateResponse = updateSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getId(), singletonMap(fieldName, value), etag);
        setSessionResponse(updateResponse);
    }

    @Step
    public void updateCustomerPropertyRelationship(String propertyId, String customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) {
        updateCustomerPropertyRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, customerId, relationshipUpdate);
    }

    @Step
    public void updateCustomerPropertyRelationshipByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) {
        String existingRelationshipId = getCustomerPropertyRelationship(customerId, propertyId).getId();
        String etag = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingRelationshipId).header(HEADER_ETAG);

        try {
            JSONObject jsonRelationshipUpdate = retrieveData(relationshipUpdate);
            Response updateResponse = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingRelationshipId, jsonRelationshipUpdate, etag);
            setSessionResponse(updateResponse);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }

    }

    public void propertyIsUpdateForCustomerWithInvalidEtag(PropertyDto p, String customerId, String fieldName, String value) {
        CustomerPropertyRelationshipDto existingCustomerProperty = getCustomerPropertyForCustomer(customerId, p.getId());
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getId(), data, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void relationExistsBetweenUserAndCustomer(String userId, String customerId, Boolean isPrimary, Boolean isActive) {
        CustomerUserRelationshipDto existingCustomerUser = getUserForCustomer(customerId, userId);
        if (existingCustomerUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("CustomerUser cannot be deleted " + deleteResponse.getBody().asString());
            }
        }
        Response createResponse = addUserToCustomer(userId, customerId, isPrimary, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerUser cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public CustomerUserRelationshipDto getUserForCustomer(String customerId, String userId) {
        Response customerUserResponse = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_id==" + userId, null, null, null);
        return stream(customerUserResponse.as(CustomerUserRelationshipDto[].class)).findFirst().orElse(null);
    }

    @Step
    public void getPropertyForCustomerByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId) {
        CustomerPropertyRelationshipDto customerPropertyRelationship = getCustomerPropertyRelationship(customerId, propertyId);
        assertThat(customerPropertyRelationship, is(notNullValue()));
        Response response = getSecondLevelEntityByUserForApp(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, customerPropertyRelationship.getId());
        setSessionResponse(response);
    }

    public void propertyIsgotForCustomerWithTypeWithEtagAfterUpdate(String propertyId, String customerId, String type) {
        String eTag = getSecondLevelEntityEtag(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("valid_to", "2200-01-01");

        updateSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, mapForUpdate, eTag);

        Response response = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId);
        setSessionResponse(response);
    }

    @Step
    public void getCustomerPropertyWithRelationshipId(String customerId, String relationshipId) {
        Response response = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, relationshipId);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotByUserForApp(String userId, String applicationVersionId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }


    @Step
    public void listOfCustomerPropertySetsIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotByUserForApp(String userId, String applicationVersionId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void userIsAddedToCustomer(String userId, String customerId, Boolean isPrimary, Boolean isActive) {
        Response response = addUserToCustomer(userId, customerId, isPrimary, isActive);
        setSessionResponse(response);
    }

    @Step
    public void userIsDeletedFromCustomer(String userId, String customerId) {
        Response deleteResponse = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void userIsDeletedFromCustomerByUserForApp(String performerId, String applicationVersionId, String userId, String customerId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void updateUserCustomerRelationship(String userId, String customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        updateUserCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, customerId, userCustomerRelationshipUpdate);
    }

    @Step
    public void updateUserCustomerRelationshipByUserForApp(String performerId, String applicationVersionId, String userId, String customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userCustomerRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(customerId, SECOND_LEVEL_OBJECT_USERS, userId);
            Response response = updateSecondLevelEntityByUserForApp(performerId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_USERS, userId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserCustomerRelationshipUpdate object");
        }
    }

    @Step
    public void userDoesntExistForCustomer(String userId, String customerId) {
        CustomerUserRelationshipDto userForCustomer = getUserForCustomer(customerId, userId);
        assertNull("User should not be present in customer", userForCustomer);
    }

    @Step
    public void getCommSubscriptionForCustomerId(String customerId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(customerId,
                SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfCustomerCommSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter,
                                                         String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, limit,
                cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public List<CustomerDto> getCustomersForIds(List<String> customerIds) {
        String filter = "customer_id=in=(" + StringUtils.join(customerIds, ',') + ")";
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(CustomerDto[].class);
        return Arrays.asList(customers);
    }

    public void removeAllUsersFromCustomers(List<String> customerIds) {
        customerIds.forEach(customerId -> {
            CustomerDto customer = getCustomerById(customerId);
            if (customer != null) {
                Response customerUsersResponse = getSecondLevelEntities(customer.getId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                CustomerUserRelationshipDto[] customerUsers = customerUsersResponse.as(CustomerUserRelationshipDto[].class);
                for (CustomerUserRelationshipDto cu : customerUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(customer.getId(), SECOND_LEVEL_OBJECT_USERS, cu.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set cannot be deleted: " + deleteResponse.getBody().asString());
                    }
                }
            }
        });
    }

    @Step
    public void listOfUsersIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfUsersIsGotByUserForApp(String userId, String applicationVersionId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        CustomerUserRelationshipDto[] customerUsers = response.as(CustomerUserRelationshipDto[].class);
        int i = 0;
        for (CustomerUserRelationshipDto cu : customerUsers) {
//            userName is not part of new class - CustomerUserRelationshipDto, needs to be obtained via different endpoint
//          TODO: uncomment  assertEquals("Customeruser on index=" + i + " is not expected", usernames.get(i), cu.getUserName());
            i++;
        }
    }

    public void listOfCustomerApiSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_API_SUBSCRIPTION, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void fieldNameHasValueForPropertyForCustomer(String fieldName, String value, String propertyId, String customerId) {
        CustomerDto c = getCustomerById(customerId);
        CustomerPropertyRelationshipDto cp = getCustomerPropertyForCustomer(c.getId(), propertyId);

        switch (fieldName) {
            case "valid_from": {
                assertEquals(value, cp.getValidFrom().format( DateTimeFormatter.ISO_LOCAL_DATE));
                break;
            }
            case "valid_to": {
                assertEquals(value, cp.getValidTo().format( DateTimeFormatter.ISO_LOCAL_DATE ));
                break;
            }
            default:
                fail("Bad field for customer property");
        }
    }

    public void relationBetweenUserAndCustomerIsDeleted(String userId, String customerId) {
        Response resp = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    public void customerWithIdDoesNotExist(String customerId) {
        Response resp = getEntity(customerId);
        if (resp.getStatusCode() == HttpStatus.SC_OK) {
            fail("Customer should not be present, but it is!");
        }
    }

    public void customerWithIdIsUpdatedWithOutdatedEtag(String customerId) throws JsonProcessingException {
        CustomerUpdateDto updateData = new CustomerUpdateDto();
        updateData.setNotes("UpdatedNotes");

        Response tempResp = getEntity(customerId);
        Response firstUpdate = updateEntity(customerId, retrieveData(updateData).toString(), tempResp.getHeader(HEADER_ETAG));
        Response secondUpdate = updateEntity(customerId, retrieveData(updateData).toString(), tempResp.getHeader(HEADER_ETAG));
        setSessionResponse(secondUpdate);
    }

    public void getCustomerUserRelationByUser(String requestorId, String customerId, String targetUserId) {
        Response response = getSecondLevelEntityByUser( requestorId, customerId, SECOND_LEVEL_OBJECT_USERS, targetUserId);
        setSessionResponse(response);
    }
    /*public void checkCustomerActivity(String customerId, boolean activity) {
        CustomerDto customer = getEntity(customerId).as(CustomerDto.class);
        assertEquals(customer.getIsActive(), activity);
    }*/

    public void propertyIsgotForCustomer(String propertyId, String customerId) {
        setAccessTokenParamFromSession();
        CustomerPropertyRelationshipDto customerPropertyRelationship = getCustomerPropertyForCustomer(customerId, propertyId);
        Response response = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, customerPropertyRelationship.getId());
        setSessionResponse(response);
    }

    public void removeUserFromCustomerByUserForApp(String requestorId, String appVersionId, String customerId, String targetUserId) {
        setSessionResponse(deleteSecondLevelEntityByUserForApplication(requestorId, appVersionId, customerId, SECOND_LEVEL_OBJECT_USERS, targetUserId, null));
    }
}
