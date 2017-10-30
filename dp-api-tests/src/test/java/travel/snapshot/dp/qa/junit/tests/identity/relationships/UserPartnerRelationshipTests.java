package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipUpdateDto;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_partner_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPartnerRelationshipTests extends CommonTest{
    private UUID createdPartnerId;
    private UUID createdUserId;
    private UserPartnerRelationshipCreateDto testUserPartnerRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPartnerId = entityIsCreated(testPartner1);
        createdUserId = entityIsCreated(testUser1);
        testUserPartnerRelationship = constructUserPartnerRelationshipDto(createdUserId, createdPartnerId, true);
    }

    @Test
    public void userPartnerRelationshipCRUD() {
        UUID relationId = entityIsCreated(testUserPartnerRelationship);
        UserPartnerRelationshipCreateDto returnedRelationship = getSessionResponse().as(UserPartnerRelationshipDto.class);
        assertThat(returnedRelationship.getPartnerId(), is(createdPartnerId));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPartnerRelationshipUpdateDto update = constructUserPartnerRelationshipUpdateDto(false);
        entityIsUpdated(USER_PARTNER_RELATIONSHIPS_PATH, relationId, update);
        entityIsDeleted(USER_PARTNER_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    public void createUserPartnerRelationshipErrors() {
        testUserPartnerRelationship = constructUserPartnerRelationshipDto(NON_EXISTENT_ID, createdPartnerId, true);
        entityIsCreated(testUserPartnerRelationship);
        testUserPartnerRelationship = constructUserPartnerRelationshipDto(createdUserId, NON_EXISTENT_ID, true);
        createEntity(USER_PARTNER_RELATIONSHIPS_PATH, testUserPartnerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }
}
