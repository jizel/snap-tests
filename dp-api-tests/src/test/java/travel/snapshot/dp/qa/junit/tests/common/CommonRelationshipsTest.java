package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.MEMBERSHIP;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;

import java.util.Arrays;
import java.util.List;

/**
 * Common tests for all relationships endpoints
 */
public class CommonRelationshipsTest extends CommonTest {


    protected static final List<String> ALL_RELATIONSHIPS_ENDPOINTS = Arrays.asList(
            CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
            PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
            USER_CUSTOMER_RELATIONSHIPS_PATH,
            USER_PARTNER_RELATIONSHIPS_PATH,
            USER_PROPERTY_RELATIONSHIPS_PATH,
            USER_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_USER_RELATIONSHIPS_PATH
    );

    private PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship1;
    private PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship2;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship1;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship2;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship3;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship1;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship2;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship1;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship2;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship3;
    private UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship1;
    private UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship2;
    private UserGroupUserRelationshipCreateDto testUserGroupUserRelationship1;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship1;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship2;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship3;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship4;
    private UserPartnerRelationshipCreateDto testUserPartnerRelationship;
    private UserGroupPropertySetRelationshipCreateDto testUserGroupPropertySetRelationship;


//    Help methods

    protected void constructAndCreateTestRelationshipsDtos() throws Exception {
        //        Prepare data for tests - basic entities
        entityIsCreated(testProperty1);
        entityIsCreated(testProperty2);
        entityIsCreated(testCustomer1);
        entityIsCreated(testCustomer2);
        entityIsCreated(testUser1);
        entityIsCreated(testUser2);
        entityIsCreated(testPropertySet1);
        entityIsCreated(testUserGroup1);
        entityIsCreated(testPartner1);
        
//        Construct DTOs
        testCustomerPropertyRelationship1 = constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty1.getId(), true, ASSET_MANAGEMENT, validFrom, validTo);
        testCustomerPropertyRelationship2 = constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty2.getId(), true, CHAIN, validFrom, validTo);
        testCustomerPropertyRelationship3 = constructCustomerPropertyRelationshipDto(
                testCustomer2.getId(), testProperty2.getId(), true, MEMBERSHIP, validFrom, validTo);

        testUserPropertySetRelationship1 = constructUserPropertySetRelationshipDto(testUser1.getId(), testPropertySet1.getId(), true);
        testUserPropertySetRelationship2 = constructUserPropertySetRelationshipDto(testUser2.getId(), testPropertySet1.getId(), true);
        testPropertySetPropertyRelationship1 = constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty1.getId(), true);
        testPropertySetPropertyRelationship2 = constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty2.getId(), true);

        testUserGroupPropertySetRelationship = constructUserGroupPropertySetRelationship(testUserGroup1.getId(), testPropertySet1.getId(), true);

        testUserGroupPropertyRelationship1 = constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty1.getId(), true);
        testUserGroupPropertyRelationship2 = constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty2.getId(), true);
        testUserGroupUserRelationship1 = constructUserGroupUserRelationship(testUserGroup1.getId(), testUser1.getId(), true);

        testUserPropertyRelationship1 = constructUserPropertyRelationshipDto(testUser1.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship2 = constructUserPropertyRelationshipDto(testUser1.getId(), testProperty2.getId(), true);
        testUserPropertyRelationship3 = constructUserPropertyRelationshipDto(testUser2.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship4 = constructUserPropertyRelationshipDto(testUser2.getId(), testProperty2.getId(), true);

        testUserPartnerRelationship = constructUserPartnerRelationshipDto(testUser1.getId(), testPartner1.getId(), true);

        //        Relationships - create via api
        entityIsCreated(testCustomerPropertyRelationship1);
        entityIsCreated(testCustomerPropertyRelationship2);
        entityIsCreated(testCustomerPropertyRelationship3);

        entityIsCreated(testPropertySetPropertyRelationship1);
        entityIsCreated(testPropertySetPropertyRelationship2);

        entityIsCreated(testUserGroupPropertyRelationship1);
        entityIsCreated(testUserGroupPropertyRelationship2);

        entityIsCreated(testUserGroupPropertySetRelationship);

        entityIsCreated(testUserGroupUserRelationship1);

        entityIsCreated(testUserPartnerRelationship);

        entityIsCreated(testUserPropertyRelationship1);
        entityIsCreated(testUserPropertyRelationship2);
        entityIsCreated(testUserPropertyRelationship3);
        entityIsCreated(testUserPropertyRelationship4);

        entityIsCreated(testUserPropertySetRelationship1);
        entityIsCreated(testUserPropertySetRelationship2);
    }
}
