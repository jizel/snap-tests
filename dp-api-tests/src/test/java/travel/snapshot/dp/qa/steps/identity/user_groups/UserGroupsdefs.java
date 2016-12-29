package travel.snapshot.dp.qa.steps.identity.user_groups;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.RoleIdDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.user_groups.UserGroupsSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vlcek on 5/9/2016.
 */
public class UserGroupsdefs {

    private static final String USER_ID = "userId";
    private static final String USER_GROUP_ID = "userGroupId";

    @Steps
    private UserGroupsSteps userGroupSteps;

    @Steps
    private UsersSteps usersSteps;

    // ------------------------- GIVEN ------------------------------

    @Given("^The following user groups exist$")
    public void The_following_user_groups_exist(List<UserGroupDto> userGroups) throws Throwable {
        userGroupSteps.followingUserGroupsExist(userGroups);
    }

    @Given("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" exists with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyExistsWithIsActive(String userGroupName, String propertyId, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.relationshipGroupPropertyExist(userGroup.getUserGroupId(), propertyId, isActive);
    }

    @Given("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" exists with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetExistsWithIsActive(String userGroupId, String propertySetId, Boolean isActive) throws Throwable {
        userGroupSteps.relationshipGroupPropertySetExist(userGroupId, propertySetId, isActive);
    }

    // ------------------------- WHEN ------------------------------

    @When("^The following user group is created$")
    public void userGroupWithIdIsCreated(List<UserGroupDto> userGroup) throws Throwable {
        userGroupSteps.followingUserGroupIsCreated(userGroup.get(0));
    }

    @When("^User group with id \"([^\"]*)\" is got$")
    public void userGroupWithIdIsGot(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdGot(userGroupId);

    }

    @When("^User group with id \"([^\"]*)\" is got with etag$")
    public void userGroupWithIdIsGotWithEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdGotWithEtag(userGroupId);
    }

    @When("^User group with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void userGroupWithIdIsGotForEtagUpdatedAndGotWithPreviousEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdIsGotWithEtagAfterUpdate(userGroupId);
    }

    @When("^List of user groups is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfUserGroupsIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        userGroupSteps.listUserGroupsIsGot(limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of relationships userGroups-Roles for userGroup \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRelationshipsUserGroupsRolesIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userGroupId,
                                                                                                      @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                      @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                      @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                      @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                      @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        userGroupSteps.listGroupRoleIsGot(userGroupId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^User group with id \"([^\"]*)\" is activated$")
    public void userGroupWithIdIsActivated(String userGroupId) throws Throwable {
        userGroupSteps.setUserGroupActiveField(userGroupId, true);
    }

    @When("^User group with id \"([^\"]*)\" is deactivated$")
    public void userGroupWithIdIsDeactivated(String userGroupId) throws Throwable {
        userGroupSteps.setUserGroupActiveField(userGroupId, false);
    }

    @When("^User group with id \"([^\"]*)\" is deleted$")
    public void userGroupWithIdIsDeleted(String userGroupId) throws Throwable {
        userGroupSteps.deleteUserGroup(userGroupId);
    }

    @When("^User group with id \"([^\"]*)\" is updated with following data$")
    public void userGroupWithIdIsUpdatedWithFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.updateUserGroup(userGroupId, userGroups.get(0));
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndPropertyIsGot(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.getUserGroupsProperty(userGroupId, propertyId);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndPropertySetIsGot(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.getUserGroupsPropertySet(userGroupId, propertySetId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is created$")
    public void relationBetweenUserGroupAndRoleIsCreated(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.relationshipGroupRoleExist(userGroupId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" exists$")
    public void relationBetweenUserGroupAndRoleExists(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.relationshipGroupRoleExist(userGroupId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyIsCreatedWithIsActive(String userGroupId, @Transform(NullEmptyStringConverter.class) String propertyId, Boolean isActive) throws Throwable {
        userGroupSteps.relationshipGroupPropertyExist(userGroupId, propertyId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsCreatedWithIsActive(String userGroupId, @Transform(NullEmptyStringConverter.class) String propertySetId, Boolean isActive) throws Throwable {
        userGroupSteps.relationshipGroupPropertySetExist(userGroupId, propertySetId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndPropertyIsDeleted(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.relationshipGroupPropertyIsDeleted(userGroupId, propertyId);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndPropertySetIsDeleted(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.relationshipGroupPropertySetIsDeleted(userGroupId, propertySetId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndRoleIsDeleted(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.deleteUserGroupRoleRelationship(userGroupId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is activated$")
    public void relationBetweenUserGroupAndPropertyIsActivated(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.setGroupPropertyActivity(userGroupId, propertyId, true);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is activated$")
    public void relationBetweenUserGroupAndPropertySetIsActivated(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.setGroupPropertySetActivity(userGroupId, propertySetId, true);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is deactivated$")
    public void relationBetweenUserGroupAndPropertyIsDeactivated(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.setGroupPropertyActivity(userGroupId, propertyId, false);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is deactivated$")
    public void relationBetweenUserGroupAndPropertySetIsDeactivated(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.setGroupPropertySetActivity(userGroupId, propertySetId, false);
    }

    // ------------------------- THEN ------------------------------

    @Then("^There are \"([^\"]*)\" user groups returned$")
    public void thereAreUserGroupsReturned(Integer userGroupsCount) throws Throwable {
        userGroupSteps.numberOfEntitiesInResponse(UserGroupDto.class, userGroupsCount);
    }

    @Then("^There are user groups with following description returned in order: \"([^\"]*)\"$")
    public void thereAreUserGroupsWithFollowingDescriptionReturnedInOrder(List<String> order) throws Throwable {
        userGroupSteps.responseSortByDescription(order);
    }

    @Then("^User group with id \"([^\"]*)\" is active$")
    public void userGroupWithIdIsActive(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupActiveField(userGroupId, true);
    }

    @Then("^User group with id \"([^\"]*)\" is not active$")
    public void userGroupWithIdIsNotActive(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupActiveField(userGroupId, false);
    }

    @Then("^User group with id \"([^\"]*)\" is no more exists$")
    public void userGroupWithIdIsNoMoreExists(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupExistency(userGroupId, false);
    }

    @Then("^User group with id \"([^\"]*)\" contains following data$")
    public void userGroupWithIdContainsFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.checkUserGroupData(userGroupId, userGroups.get(0));
    }

    @Then("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is no more exists$")
    public void relationBetweenUserGroupAndPropertyIsNoMoreExists(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.checkGroupPropertyExistence(userGroupId, propertyId);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is no more exists$")
    public void relationBetweenUserGroupAndPropertySetIsNoMoreExists(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.checkGroupPropertySetExistence(userGroupId, propertySetId);
    }

    @Then("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is established$")
    public void relationBetweenUserGroupAndRoleIsEstablished(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.checkUserGroupRoleRelationExistency(userGroupId, roleId, true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is not established$")
    public void relationBetweenUserGroupAndRoleIsNotEstablished(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.checkUserGroupRoleRelationExistency(userGroupId, roleId, false);
    }

    @Then("^There are \"([^\"]*)\" relationships returned$")
    public void thereAreRelationshipsReturned(int numberOfRoles) throws Throwable {
        userGroupSteps.numberOfEntitiesInResponse(RoleIdDto.class, numberOfRoles);
    }

    @Then("^There are relationships start with following IDs returned in order: \"([^\"]*)\"$")
    public void thereAreRelationshipsStartWithFollowingIDsReturnedInOrder(List<String> order) throws Throwable {
        userGroupSteps.responseSortById(order);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is activate$")
    public void relationBetweenUserGroupAndPropertyIsActivate(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroupId, propertyId, true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is not activate$")
    public void relationBetweenUserGroupAndPropertyIsNotActivate(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroupId, propertyId, false);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is activate$")
    public void relationBetweenUserGroupAndPropertySetIsActivate(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.checkuserGroupPropertySetRelationActivity(userGroupId, propertySetId, true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is not activate$")
    public void relationBetweenUserGroupAndPropertySetIsNotActivate(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.checkuserGroupPropertySetRelationActivity(userGroupId, propertySetId, false);
    }

    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\"$")
    public void userIsAddedToUserGroupWithId(String userName, String userGroupName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.addUserToUserGroup(ids.get(USER_ID), ids.get(USER_GROUP_ID), true);
    }

    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\" as isActive \"([^\"]*)\"$")
    public void userIsAddedToUserGroupWithIdAsIsActive(String userName, String userGroupName, Boolean isActive) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.addUserToUserGroup(ids.get(USER_ID), ids.get(USER_GROUP_ID), isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndUserIsGot(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.getUserGroupsUser(ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user with id \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndUserWithIdIsCreatedWithIsActive(String userGroupName, String userId, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat("UserGroup with name " + userGroupName + " not found. ", userGroup, is(not(nullValue())));

        userGroupSteps.addUserToUserGroup(userId, userGroup.getUserGroupId(), isActive);
    }

    @When("^User \"([^\"]*)\" is removed from userGroup \"([^\"]*)\"$")
    public void userIsRemovedFromUserGroup(String userName, String userGroupName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.relationshipGroupUserIsDeleted(ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is activated$")
    public void relationBetweenUserGroupAndUserIsActivated(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.setUserGroupUserActivity(ids.get(USER_GROUP_ID), ids.get(USER_ID), true);
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is deactivated$")
    public void relationBetweenUserGroupAndUserIsDeActivated(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.setUserGroupUserActivity(ids.get(USER_GROUP_ID), ids.get(USER_ID), false);
    }

    @And("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is active$")
    public void relationBetweenUserGroupAndUserIsActive(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        Boolean isActive = userGroupSteps.getUserGroupUserRelationIsActive(ids.get(USER_GROUP_ID), ids.get(USER_ID));
        assertThat("User is not active", isActive, is(true));
    }

    @And("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is not active$")
    public void relationBetweenUserGroupAndUserIsNotActive(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        Boolean isActive = userGroupSteps.getUserGroupUserRelationIsActive(ids.get(USER_GROUP_ID), ids.get(USER_ID));
        assertThat("User is not active", isActive, is(false));
    }

    @Given("^The following user group is created by user \"([^\"]*)\"$")
    public void theFollowingUserGroupIsCreatedByUser(String userName, List<UserGroupDto> userGroups) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(userName);
        assertThat("User with username " + userName + " is null. ", user, is(not(nullValue())));

        userGroupSteps.followingUserGroupIsCreatedByUser(user.getUserId(), userGroups.get(0));
    }

//    Help methods
    private Map<String, String> getNonNullIdsFromNames(String userGroupName, String userName) {
        UserDto user = usersSteps.getUserByUsername(userName);
        assertThat("User with username " + userName + " is null. ", user, is(not(nullValue())));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat("UserGroup with name " + userGroupName + " not found. ", user, is(not(nullValue())));

        Map<String, String> userGroupUserIds = new HashMap<>();
        userGroupUserIds.put(USER_ID, user.getUserId());
        userGroupUserIds.put(USER_GROUP_ID, userGroup.getUserGroupId());

        return userGroupUserIds;
    }
}
