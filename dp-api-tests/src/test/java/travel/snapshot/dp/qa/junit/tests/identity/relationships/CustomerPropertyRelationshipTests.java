package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Integration tests for /identity/customer_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class CustomerPropertyRelationshipTests extends CommonTest {
    private UUID createdProperty1Id;
    private UUID createdCustomer1Id;
    private LocalDate validFrom = LocalDate.now();
    private LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdProperty1Id = commonHelpers.entityIsCreated(testProperty1);
        createdCustomer1Id = commonHelpers.entityIsCreated(testCustomer1);
        testCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, createdProperty1Id, true, ASSET_MANAGEMENT, validFrom, validTo);
    }

    @Test
    public void createCustomerPropertyRelationship() {
        Response createResponse = commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        CustomerPropertyRelationshipCreateDto returnedRelationship = createResponse.as(CustomerPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1Id));
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1Id));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getType(), is(ASSET_MANAGEMENT));
        assertThat(returnedRelationship.getValidFrom(), is(validFrom));
        assertThat(returnedRelationship.getValidTo(), is(validTo));

        CustomerPropertyRelationshipCreateDto requestedRelationship = commonHelpers.getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createCustomerPropertyRelationshipErrors() {
        CustomerPropertyRelationshipCreateDto invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                NON_EXISTENT_ID, createdProperty1Id, true, ASSET_MANAGEMENT, validFrom, validTo);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, NON_EXISTENT_ID, true, ASSET_MANAGEMENT, validFrom, validTo);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1Id, createdProperty1Id, true, ASSET_MANAGEMENT, validTo, validFrom);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void updateCustomerPropertyRelationship() throws Exception {
        LocalDate updatedValidFrom = LocalDate.now().minusMonths(5);
        LocalDate updatedValidTo = LocalDate.now();
        CustomerPropertyRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        CustomerPropertyRelationshipUpdateDto update = relationshipsHelpers.constructCustomerPropertyRelationshipUpdate(false, CHAIN, updatedValidFrom, updatedValidTo);

        commonHelpers.updateEntityPost(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);

        CustomerPropertyRelationshipCreateDto returnedRelationship = commonHelpers.getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, createdRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getType(), is(CHAIN));
        assertThat(returnedRelationship.getValidFrom(), is(updatedValidFrom));
        assertThat(returnedRelationship.getValidTo(), is(updatedValidTo));
    }

    @Test
    public void deleteCustomerPropertyRelationship() {
        CustomerPropertyRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        commonHelpers.deleteEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
