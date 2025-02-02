package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

public class CustomerAccessCheckByUserTests extends CommonAccessCheckByUserTest {

    UUID createdCustomerId;
    UUID userId;
    UUID propertyId;
    UUID propertySetId1;
    UUID propertySetId2;
    UUID customerPropertyRelationId;
    UUID userCustomerRelationId;
    UUID userPropertyRelationId;
    UUID userPropertySetRelationId;
    UUID roleId;
    Map<String, UUID> inaccessibles = new HashMap<>();

    @BeforeEach
    public void setUp() {
        super.setUp();
        createdCustomerId = entityIsCreated(testCustomer1);
        testUser1.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true));
        testUser2.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(createdCustomerId, true, true));
        requestorId = entityIsCreated(testUser1);
        userId = entityIsCreated(testUser2);
        propertyId = entityIsCreated(testProperty1);
        propertySetId1 = entityIsCreated(testPropertySet1);
        testPropertySet2.setCustomerId(createdCustomerId);
        propertySetId2 = entityIsCreated(testPropertySet2);
        customerPropertyRelationId = entityIsCreated(constructCustomerPropertyRelationshipDto(createdCustomerId, propertyId, true, CHAIN, validFrom, validTo));
        entityIsCreated(constructCustomerPropertyRelationshipDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, true, CHAIN, validFrom, validTo));
        userPropertyRelationId = entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, true));
        userPropertySetRelationId = entityIsCreated(constructUserPropertySetRelationshipDto(userId, propertySetId2, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId1, true));
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", userId.toString())).build();
        userCustomerRelationId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, params).get(0).getId();
        inaccessibles.put(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationId);
        inaccessibles.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, customerPropertyRelationId);
        inaccessibles.put(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelationId);
        inaccessibles.put(USERS_PATH, userId);
        inaccessibles.put(PROPERTIES_PATH, propertyId);
        inaccessibles.put(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPropertySetRelationId);

        /*
         We end up with the following topology:
             C1          C2
            /|\         /|\
           / | \       / | \
          /  |  \     /  |  \
         P1-U1-PS1   P2--U2-PS2

         U1 being the context user
        */
    }

    @Test
    void getAccessibleAndInacessibleEntities() {
        inaccessibles.forEach((endpoint, id) -> {
            // Filtering
            assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, endpointDtoMap.get(endpoint), emptyQueryParams())).hasSize(1);
            headerIs(TOTAL_COUNT_HEADER, "1");
            // getting inaccessible entities
            getEntityFails(requestorId, endpoint, id);
        });
    }

    /**
     * Make sure that adding relations with other properties and property sets
     * would not give a user an access to their relations with inaccessible entities as well
     */
    @Test
    void testExtraAccessibleProperty() {
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId2, true));
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, PropertyDto.class, emptyQueryParams())).hasSize(2);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams())).hasSize(2);
        inaccessibles.remove(PROPERTIES_PATH);
        inaccessibles.remove(PROPERTY_SETS_PATH);
        inaccessibles.forEach((endpoint, id) -> {
            getEntityFails(requestorId, endpoint, id);
        });
    }

    @Test
    void implicitAccessThroughCustomerHierarchy() {
        CustomerUpdateDto update = new CustomerUpdateDto();
        update.setParentId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        entityIsUpdated(CUSTOMERS_PATH, createdCustomerId, update);
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId2, true));
        /*
         We end up with the following topology:
             C1
            / \
           /   \
          /     \
         U1--P2-C2
          \  | \/|
           \ | /\|
           PS2---U2

         U1 would be the context user and will request entities and relations belonging to Customer2
        */
        // Do not request entities with explicit access
        inaccessibles.remove(PROPERTIES_PATH);
        inaccessibles.remove(PROPERTY_SETS_PATH);
        inaccessibles.forEach((endpoint, id) -> {
            getEntitySucceeds(requestorId, endpoint, id);
        });
    }

    @Test
    void accessThroughUserGroup() {
        testUserGroup1.setCustomerId(createdCustomerId);
        UUID groupId = entityIsCreated(testUserGroup1);
        UUID relationId = entityIsCreated(constructUserGroupUserRelationship(groupId, requestorId, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId2, true));
        // Do not request entities with explicit access
        inaccessibles.remove(PROPERTIES_PATH);
        inaccessibles.remove(PROPERTY_SETS_PATH);
        /*
         We end up with the following topology:
             C1--U1--UG--C2
            /|  / \      /|\
           / | /   \    / | \
          /  |/     \  /  |  \
         PS1--P1     P2--U2-PS2

         U1 will be the context user and will request entities and relations from the right cluster
        */
        inaccessibles.forEach((endpoint, id) -> {
            getEntitySucceeds(requestorId, endpoint, id);
        });
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        inaccessibles.forEach((endpoint, id) -> {
            getEntityFails(requestorId, endpoint, id);
        });
    }

    @Test
    void customerUpdateDeleteByUser() {
        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setWebsite("https://newsite.com");
        updateEntityFails(requestorId, CUSTOMERS_PATH, createdCustomerId, updateDto);
        updateEntitySucceeds(requestorId, CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID, updateDto);
        deleteEntityFails(requestorId, CUSTOMERS_PATH, createdCustomerId, SC_NOT_FOUND);
        deleteEntityFails(requestorId, CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID, SC_CONFLICT);
    }

    @Test
    void userPropertyCRUDWithAccessChecks() {
        UUID createdUserId = entityIsCreated(testUser3);
        createEntitySucceeds(requestorId, constructUserPropertyRelationshipDto(createdUserId, DEFAULT_PROPERTY_ID, true));
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", String.valueOf(createdUserId))).build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, params)
                .get(0).getId();
        createEntityFails(requestorId, constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true));
        updateEntityFails(userId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        updateEntitySucceeds(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        deleteEntityFails(userId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId, SC_NOT_FOUND);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    void userPropertySetCRUDWithAccessChecks() {
        UUID createdUserId = entityIsCreated(testUser3);
        createEntitySucceeds(requestorId, constructUserPropertySetRelationshipDto(createdUserId, propertySetId1, true));
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", String.valueOf(createdUserId))).build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, params)
                .get(0).getId();
        createEntityFails(requestorId, constructUserPropertySetRelationshipDto(userId, propertySetId1, true));
        updateEntityFails(userId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        updateEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        deleteEntityFails(userId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, SC_NOT_FOUND);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
    }
}
