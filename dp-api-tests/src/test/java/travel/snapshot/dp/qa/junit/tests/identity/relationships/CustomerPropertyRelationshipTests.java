package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipUpdate;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Integration tests for /identity/customer_property_relationships endpoint
 */
public class CustomerPropertyRelationshipTests extends CommonTest {
    private UUID createdProperty1Id;
    private UUID createdCustomer1Id;
    private LocalDate validFrom = LocalDate.now();
    private LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdProperty1Id = entityIsCreated(testProperty1);
        createdCustomer1Id = entityIsCreated(testCustomer1);
        testCustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, createdProperty1Id, true, ASSET_MANAGEMENT, validFrom, validTo);
    }

    @Test
    public void createCustomerPropertyRelationship() {
        Response createResponse = createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        CustomerPropertyRelationshipCreateDto returnedRelationship = createResponse.as(CustomerPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1Id));
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1Id));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getType(), is(ASSET_MANAGEMENT));
        assertThat(returnedRelationship.getValidFrom(), is(validFrom));
        assertThat(returnedRelationship.getValidTo(), is(validTo));

        CustomerPropertyRelationshipCreateDto requestedRelationship = getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
        createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship)
                .then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_VALUES));
    }

    @Test
    public void createCustomerPropertyRelationshipErrors() {
        CustomerPropertyRelationshipCreateDto invalidCustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(
                NON_EXISTENT_ID, createdProperty1Id, true, ASSET_MANAGEMENT, validFrom, validTo);
        createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, NON_EXISTENT_ID, true, ASSET_MANAGEMENT, validFrom, validTo);
        createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, createdProperty1Id, true, ASSET_MANAGEMENT, validTo, validFrom);
        createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void updateCustomerPropertyRelationship() throws Exception {
        LocalDate updatedValidFrom = LocalDate.now().minusMonths(5);
        LocalDate updatedValidTo = LocalDate.now();
        CustomerPropertyRelationshipCreateDto createdRelationship = entityIsCreatedAs(CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        CustomerPropertyRelationshipUpdateDto update = constructCustomerPropertyRelationshipUpdate(false, CHAIN, updatedValidFrom, updatedValidTo);
        updateEntityWithEtag(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update, "invalid")
                .then()
                .statusCode(SC_PRECONDITION_FAILED)
                .assertThat().body(RESPONSE_CODE, is(CC_INVALID_ETAG));

        entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        CustomerPropertyRelationshipCreateDto returnedRelationship = getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, createdRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getType(), is(CHAIN));
        assertThat(returnedRelationship.getValidFrom(), is(updatedValidFrom));
        assertThat(returnedRelationship.getValidTo(), is(updatedValidTo));
    }

    @Test
    public void deleteCustomerPropertyRelationship() {
        CustomerPropertyRelationshipCreateDto createdRelationship = entityIsCreatedAs(CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
    }
}
