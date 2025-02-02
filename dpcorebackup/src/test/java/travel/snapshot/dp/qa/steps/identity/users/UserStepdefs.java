package travel.snapshot.dp.qa.steps.identity.users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static travel.snapshot.dp.api.identity.model.UserType.PARTNER;
import static travel.snapshot.dp.api.identity.model.UserType.SNAPSHOT;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.REQUESTOR_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.TARGET_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.helpers.Resolvers;
import travel.snapshot.dp.qa.helpers.RoleType;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.partners.PartnerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.roles.RoleBaseSteps;
import travel.snapshot.dp.qa.serenity.users.UserRolesSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserStepdefs {

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private RoleBaseSteps roleBaseSteps;

    @Steps
    private UserRolesSteps userRolesSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionsSteps;

    @Steps
    private Resolvers resolvers;

    @Steps
    private PartnerSteps partnerSteps;


    @Given("^The following users exist for customer \"([^\"]*)\"(?: as primary \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?$")
    public void theFollowingUsersExistForCustomer(String customerName, String isPrimaryString, String isActiveString, List<UserCreateDto> users) throws Throwable {
//        Cucumber turns is_active to false when not present we want true by default in tests.
        UUID customerId = customerSteps.resolveCustomerId(customerName);
        Boolean isPrimary = ((isPrimaryString==null) ? false : Boolean.valueOf(isPrimaryString));
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        usersSteps.followingUsersExist(users, customerId, isPrimary, isActive);
    }

    @When("^The following user is created(?: for customer \"([^\"]*)\")?(?: as primary \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?$")
    public void User_is_created(UUID customerId, Boolean isPrimary, String isActiveString, List<UserCreateDto> users) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        usersSteps.createUserWithCustomer(users.get(0), customerId, isPrimary, isActive);
    }

    @Then("^Body contains user type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_user_type_with_value(String name, String value) throws Throwable {
        usersSteps.bodyContainsUserWith(name, value);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same user$")
    public void header_is_set_and_contains_the_same_user(String header) throws Throwable {
        usersSteps.compareUserOnHeaderWithStored(header);
    }

    @When("^User \"([^\"]*)\" is deleted$")
    public void User_with_name_name_is_deleted(String username) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        usersSteps.deleteUser(userId);
    }

    @When("^User \"([^\"]*)\" is deleted with ETAG \"([^\"]*)\"$")
    public void userWithIsDeletedWithOutdatedETAG(String username, String etag) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        usersSteps.deleteUserWithEtag(userId, etag);
    }

    @Then("^User with same id doesn't exist$")
    public void User_with_same_id_does_not_exist() throws Throwable {
        usersSteps.userIdInSessionDoesNotExist();
    }

    @When("^Nonexistent user is deleted$")
    public void Nonexistent_user_is_deleted() throws Throwable {
        usersSteps.deleteUser(NON_EXISTENT_ID);
    }

    @When("^User \"([^\"]*)\" is updated with data(?: by user \"([^\"]*)\")?$")
    public void User_with_user_name_updated_with_data(String userName, String performerName, List<UserDto> users) throws Throwable {
        UUID targetId = usersSteps.resolveUserId(userName);
        if (performerName == null) {
            usersSteps.updateUser(targetId, users.get(0));
        } else {
            UUID performerId = usersSteps.resolveUserId(performerName);
            usersSteps.updateUserByUser(performerId, targetId, users.get(0));
        }
    }

    @Then("^Updated user \"([^\"]*)\" has data$")
    public void Updated_user_with_user_name_has_data(String userName, List<UserDto> users) throws Throwable {
        usersSteps.userWithUserNameHasData(userName, users.get(0));
    }

    @When("^User \"([^\"]*)\" is updated with data if updated before$")
    public void User_with_user_name_is_updated_with_data_if_updated_before(String userName, List<UserDto> users) throws Throwable {
        usersSteps.updateUserWithUserNameIfUpdatedBefore(userName, users.get(0));
    }

    @When("^User \"([^\"]*)\" is got$")
    public void User_with_username_is_got(String username) throws Throwable {
        usersSteps.userWithUsernameIsGot(username);
    }

    @When("^Nonexistent user id is got$")
    public void Nonexistent_user_id_is_got() throws Throwable {
        usersSteps.userWithIdIsGot(NON_EXISTENT_ID);
    }

    @When("^List of users is got(?: with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void List_of_users_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                             @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                             @Transform(NullEmptyStringConverter.class) String filter,
                                                                                             @Transform(NullEmptyStringConverter.class) String sort,
                                                                                             @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                             @Transform(NullEmptyStringConverter.class) String userName,
                                                                                             @Transform(NullEmptyStringConverter.class) String appVersionName) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        UUID appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        usersSteps.listOfUsersIsGotByUserForApp(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) users returned$")
    public void There_are_returned_users_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(UserDto.class, count);
    }

    @Then("^There are (\\d+) userCustomers returned$")
    public void There_are_returned_userCustomers_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(CustomerUserRelationshipPartialDto.class, count);
    }

    @Then("^There are users with following usernames returned in order: (.*)$")
    public void There_are_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        usersSteps.usernamesAreInResponseInOrder(usernames);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is removed from user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_removed_from_user_with_username_with_relationship_type_and_entity_with_code(String roleName, UUID applicationId, String username, String relationshipType, UUID entityId) throws Throwable {
        RoleBaseDto role = roleBaseSteps.getRoleByName(roleName);
        usersSteps.roleIsDeletedFromUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @Then("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is not there for user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_not_there_for_user_with_username_with_relationship_type_and_entity_with_code(String roleName, UUID applicationId, String username, String relationshipType, UUID entityId) throws Throwable {
        RoleBaseDto role = roleBaseSteps.getRoleByName(roleName);
        usersSteps.roleDoesntExistForUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^Nonexistent role is removed from user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Nonexistent_role_is_removed_from_user_with_username_with_relationship_type_and_entity_with_code(String username, String relationshipType, UUID entityId) throws Throwable {
        RoleBaseDto role = new CustomerRoleDto();
        role.setId(NON_EXISTENT_ID);
        usersSteps.roleIsDeletedFromUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^List of roles for user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_roles_for_user_with_username_with_relationship_type_and_entity_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String username, String relationshipType, UUID entityId,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {

        usersSteps.listOfRolesIsGotForRelationshipTypeEntityWIth(username, relationshipType, entityId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) user roles returned$")
    public void There_are_returned_user_roles_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(RoleRelationshipDto.class, count);
    }

    @Then("^There are user roles with following role names returned in order: (.*)$")
    public void There_are_user_roles_with_following_role_names_returned_in_order(List<String> rolenames) throws Throwable {
        usersSteps.rolenamesAreInResponseInOrder(rolenames);
    }

    @Given("^The password of user \"([^\"]*)\" is \"([^\"]*)\"$")
    public void setting_the_password_of_user_to(String username, String password) {
        usersSteps.setUserPassword(usersSteps.resolveUserId(username), password);
    }

    @When("^User \"([^\"]*)\" is activated$")
    public void userWithCodeIsActivated(String name) throws Throwable {
        UUID userId = usersSteps.resolveUserId(name);
        usersSteps.setUserIsActive(userId, true);
    }

    @When("^User \"([^\"]*)\" is inactivated$")
    public void userWithIdIsInactivated(String name) throws Throwable {
        UUID userId = usersSteps.resolveUserId(name);
        usersSteps.setUserIsActive(userId, false);
    }

    @Then("^User \"([^\"]*)\" is active$")
    public void userWithIdIsActive(String name) throws Throwable {
        UUID userId = usersSteps.resolveUserId(name);
        UserDto user = usersSteps.getUserById(userId);
        assertThat("User is not active!", user.getIsActive(), is(true));
    }

    @Then("^User \"([^\"]*)\" is not active$")
    public void userWithIdIsNotActive(String name) throws Throwable {
        UUID userId = usersSteps.resolveUserId(name);
        UserDto user = usersSteps.getUserById(userId);
        assertThat("User is active but should be inactive.", user.getIsActive(), is(false));
    }

    @When("^Role with id \"([^\"]*)\" for user(?: name)? \"([^\"]*)\" and customer(?: id)? \"([^\"]*)\" is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithNameForUserNameAndCustomerIdIsAddedString(UUID roleId, String userName, UUID customerId, String isActiveString) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.createRoleBetweenUserAndCustomer(roleId, userId, customerId, isActive);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and customer id \"([^\"]*)\" is added$")
    public void roleWithIdForNotExistingUserIdAndCustomerIdIsAdded(UUID roleId, UUID customerId) throws Throwable {
        userRolesSteps.createRoleBetweenUserAndCustomer(roleId, NON_EXISTENT_ID, customerId, true);
    }

    @When("^(Nonexistent )?(?:R|r)ole with id \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndCustomerIdIsDeleted(String nonExistent, UUID roleId, String userName, UUID customerId) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.roleBetweenUserAndCustomerIsDeleted(roleId, userId, customerId, nonExistent);
    }

    @And("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndCustomerIdDoesNotExist(UUID roleId, String userName, UUID customerId) throws Throwable {
        userRolesSteps.roleBetweenUserAndCustomerNotExists(roleId, userName, customerId);
    }

    @Given("^Role with name \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithNameForUserNameAndCustomerIdIsAdded(String roleName, String userName, UUID customerId, String isActiveString) throws Throwable {
        roleBaseSteps.setRolesPathCustomer();
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        RoleBaseDto role = roleBaseSteps.getRoleByName(roleName);
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.roleNameExistsBetweenUserAndCustomer(role.getId(), userId, customerId, isActive);
    }

    @When("^List of roles for user with username \"([^\"]*)\" and customer id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndCustomerIdIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, UUID customerId,
                                                                                                               @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                               @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                               @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                               @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                               @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.getRolesBetweenUserAndCustomer(userId, customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property(?: code)? \"([^\"]*)\" is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithIdForUserNameAndPropertyCodeIsAdded(UUID roleId, String userName, String propCode, String isActiveString) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        UUID propertyId = propertySteps.resolvePropertyId(propCode);
        userRolesSteps.roleExistsBetweenUserAndProperty(roleId, userId, propertyId, isActive);
    }

    @When("^I (?:add|assign) role(?: with id)? \"([^\"]*)\" to user(?: name)? \"([^\"]*)\" and property(?: code| id)? \"([^\"]*)\"$")
    public void iAddRoleWithIdToUserNameAndPropertyCode(UUID roleId, String userName, String propertyCode) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.addRoleBetweenUserAndProperty(roleId, userId, propertyId, true);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property id \"([^\"]*)\" is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithIdForUserNameAndPropertyIdIsAdded(UUID roleId, String userName, UUID propertyId, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.roleExistsBetweenUserAndProperty(roleId, usersSteps.resolveUserId(userName), propertyId, isActive);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and property code \"([^\"]*)\" is added$")
    public void roleWithIdForNotExistingPropertyIdAndCustomerIdIsAdded(UUID roleId, String propCode) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propCode);
        userRolesSteps.addRoleBetweenUserAndProperty(roleId, NON_EXISTENT_ID, propertyId, true);
    }

    @When("^(Nonexistent )?(?:R|r)ole with id \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndPropertyCodeIsDeleted(String nonExistent, UUID roleId, String userName, String propCode) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propCode);
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.roleBetweenUserAndPropertyIsDeleted(roleId, userId, propertyId, nonExistent);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndPropertyCodeDoesNotExist(UUID roleId, String userName, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.roleBetweenUserAndPropertyNotExists(roleId, userName, prop.getId());
    }

    @Given("^Role with name \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithNameForUserNameAndPropertyCodeIsAdded(String roleName, String userName, String propCode, String isActiveString) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        UUID propertyId = propertySteps.resolvePropertyId(propCode);
        roleBaseSteps.setRolesPathProperty();
        RoleBaseDto role = roleBaseSteps.getRoleByName(roleName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.roleNameExistsBetweenUserAndProperty(role.getId(), userId, propertyId, isActive);
    }

    @When("^List of roles for user with username \"([^\"]*)\" and property code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndPropertyCodeIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, String propCode,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.getRolesBetweenUserAndProperty(userId, prop.getId(), limit, cursor, filter, sort, sortDesc);
    }

    @Given("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\"(?: for customer \"([^\"]*)\")? is added(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerIsAdded(UUID roleId, String userName, String propertySetName, UUID customerId, String isActiveString) throws Throwable {
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID userId = usersSteps.resolveUserId(userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.roleExistsBetweenUserAndPropertySet(roleId, userId, propertySetId, isActive);
    }


    @When("^I assign role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\"(?: with is(?:A|a)ctive \"([^\"]*)\")?$")
    public void iAssignRoleWithIdForUserNameAndPropertySetName(UUID roleId, String userName, String propertySetName, String isActiveString) throws Throwable {
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID userId = usersSteps.resolveUserId(userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.addRoleToUserPropertySet(roleId, userId, propertySetId, isActive);
    }

    @When("^(Nonexistent )?(?:R|r)ole with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerIsDeleted(String nonExistent, UUID roleId, String userName, String propertySetName, UUID customerId) throws Throwable {
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID userId = usersSteps.resolveUserId(userName);
        userRolesSteps.roleBetweenUserAndPropertySetIsDeleted(roleId, userId, propertySetId, nonExistent);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerDoesNotExist(UUID roleId, String userName, String propertySetName, UUID customerId) throws Throwable {;
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.roleBetweenUserAndPropertySetNotExists(roleId, userName, propertySet.getId());
    }

    @When("^List of roles for user with username \"([^\"]*)\" and property set name \"([^\"]*)\" for customer id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndPropertySetNameForCustomerCodeIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, String propertySetName, UUID customerId,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);
        UUID userId = resolvers.resolveEntityName("user", userName);

        userRolesSteps.getRolesBetweenUserAndPropertySet(userId, propertySet.getId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and property set name \"([^\"]*)\" for customer id \"([^\"]*)\" is added(?: with is(?:Aa)ctive \"([^\"]*)\")?$")
    public void roleWithIdForNotExistingUserIdAndPropertySetNameForCustomerCodeIsAdded(UUID roleId, String propertySetName, UUID customerId, String isActiveString) throws Throwable {
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.addRoleBetweenNotExistingUserAndPropertySet(roleId, NON_EXISTENT_ID, propertySetId, isActive);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set id \"([^\"]*)\" is added(?: with is(?:Aa)ctive \"([^\"]*)\")?$")
    public void roleWithIdForUserNameAndPropertySetIdIsAdded(UUID roleId, String userName, UUID propSerId, String isActiveString) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userRolesSteps.roleExistsBetweenUserAndPropertySet(roleId, userId, propSerId, isActive);
    }

    @When("^Following (snapshot|partner) user is created without customer$")
    public void followingSnapshotUserIsCreatedWithoutCustomer(String userType, List<UserCreateDto> users) throws Throwable {
        UserCreateDto user = users.get(0);
        if(userType.equals("snapshot")) {
            user.setType(SNAPSHOT);
        }
        else if (userType.equals("partner")){
            user.setType(PARTNER);
        }
        usersSteps.createUser(user);
    }

    @When("^Property set \"([^\"]*)\" is added to user \"([^\"]*)\"$")
    public void propertySetIsAddedToUser(String propertySetName, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(user, is(notNullValue()));
        assertThat(propertySet, is(notNullValue()));

        usersSteps.addPropertySetToUser(propertySet.getId(), user.getId());
    }

    @And("^There are \"([^\"]*)\" users returned$")
    public void thereAreUsersReturned(Integer userCount) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(UserDto.class, userCount);
    }

    @When("^User \"([^\"]*)\" creates user as primary \"([^\"]*)\" for customer with id \"([^\"]*)\"$")
    public void userWithUsernameCreatesUserForCustomerWithId(String userName, Boolean isPrimary, UUID customerId, List<UserCreateDto> users) throws Throwable {
        UUID performerId = usersSteps.resolveUserId(userName);
        usersSteps.createUserForCustomerByUser(performerId, customerId, users.get(0), isPrimary);
    }

    @When("^User \"([^\"]*)\" deletes user \"([^\"]*)\"$")
    public void userWithUsernameDeletesUserWithId(String performerUserName, String targetUserName) throws Throwable {
        Map<String, UUID> userIdMap = usersSteps.getUsersIds(performerUserName, targetUserName);
        usersSteps.deleteUserByUser(userIdMap.get(REQUESTOR_ID), userIdMap.get(TARGET_ID));
    }

    @When("^Relation between user \"([^\"]*)\" and customer \"([^\"]*)\" is requested by user \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserWithUsernameAndCustomerIsRequestedByUser(String targetUserName, UUID customerId, String requestorUserName, String appVersionName) throws Throwable {
        UUID appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        Map<String, UUID> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.getUserCustomerRelationByUserForApp(userIdMap.get(REQUESTOR_ID), appVersionId, customerId, userIdMap.get(TARGET_ID));
    }

    @When("^User \"([^\"]*)\" requests list of customer for user \"([^\"]*)\"$")
    public void userWithUsernameRequestsListOfCustomerForUserWithUsername(String requestorUserName, String targetUserName) throws Throwable {
        Map<String, UUID> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.listUserCustomersByUser(userIdMap.get(REQUESTOR_ID), userIdMap.get(TARGET_ID));
    }

    @When("^User \"([^\"]*)\" requests roles of user \"([^\"]*)\" for (customer|property|property set) \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userRequestsRolesOfUserForCustomer(String requestorUserName, String targetUserName, String secondLevelType, String secondLevelName, String appVersionName) throws Throwable {
        roleBaseSteps.setRolesPath(RoleType.valueOf(secondLevelType.toUpperCase().replace(" ", "_")));
        UUID secondLevelId = resolvers.resolveEntityName(secondLevelType, secondLevelName);
        UUID appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        Map<String, UUID> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.listRolesForRelationByUserForApp(userIdMap.get(REQUESTOR_ID), appVersionId, userIdMap.get(TARGET_ID), secondLevelType, secondLevelId);
    }


    @When("^User \"([^\"]*)\" assigns role \"([^\"]*)\" to relation between user \"([^\"]*)\" and (customer|property|property set) \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userAssignsRoleToUserCustomerRelationBetweenUserAtCustomer(String requestorUsername, String roleName, String targetUsername, String secondLevelType, String secondLevelName, String appVersionName) throws Throwable {
        roleBaseSteps.setRolesPath(RoleType.valueOf(secondLevelType.toUpperCase().replace(" ", "_")));
        Map<String, UUID> userIdsMap = usersSteps.getUsersIds( requestorUsername, targetUsername );
        UUID roleId = roleBaseSteps.resolveRoleId(roleName);
        UUID secondLevelId = resolvers.resolveEntityName(secondLevelType, secondLevelName);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        usersSteps.userAssignsRoleToRelationWithApp(userIdsMap.get(REQUESTOR_ID), applicationVersionId, userIdsMap.get(TARGET_ID), secondLevelType, secondLevelId, roleId);

    }


    @When("^User \"([^\"]*)\" deletes role \"([^\"]*)\" from relation between user \"([^\"]*)\" and (customer|property|property set) \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userDeletesRoleFromUserCustomerRelationBetweenUserAtCustomer(String requestorUsername, String roleName, String targetUsername, String secondLevelType, String secondLevelName, String appVersionName) throws Throwable {
        roleBaseSteps.setRolesPath(RoleType.valueOf(secondLevelType.toUpperCase().replace(" ", "_")));
        Map<String, UUID> userIdsMap = usersSteps.getUsersIds( requestorUsername, targetUsername );
        UUID roleId = roleBaseSteps.resolveRoleId(roleName);
        UUID secondLevelId = resolvers.resolveEntityName(secondLevelType, secondLevelName);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        usersSteps.userDeletesRoleFromRelationWithApp(userIdsMap.get(REQUESTOR_ID), applicationVersionId, userIdsMap.get(TARGET_ID), secondLevelType, secondLevelId, roleId);
    }

    @When("^User \"([^\"]*)\" creates user with:$")
    public void userCreatesSnapshotUser(String username, List<UserCreateDto> users) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        usersSteps.createUserByUser(userId, users.get(0));
    }

    @When("^User \"([^\"]*)\" requests(?: list of)? users for property \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userRequestsListOfUsersForProperty(String userName, String propertyName, String applicationVersionName) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        UUID propertyId = propertySteps.resolvePropertyId(propertyName);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.listUsersForPropertyByUserForApp(userId, applicationVersionId, propertyId);
    }

    @When("^User \"([^\"]*)\" adds user \"([^\"]*)\" to property \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userAddsUserToProperty(String requestorName, String targetUserName, String propertyCode, String appVersionName) throws Throwable {
        UUID requestorId = usersSteps.resolveUserId(requestorName);
        UUID targetUserId = usersSteps.resolveUserId(targetUserName);
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        propertySteps.addPropertyToUserByUserForApp(requestorId, applicationVersionId, propertyId, targetUserId);
    }

    @When("^User \"([^\"]*)\" adds user \"([^\"]*)\" to customer \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userAddsUserToCustomer(String requestorName, String targetUserName, UUID customerId, String appVersionName) throws Throwable {
        UUID requestorId = usersSteps.resolveUserId(requestorName);
        UUID targetUserId = usersSteps.resolveUserId(targetUserName);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        customerSteps.addUserToCustomerByUserForApp(requestorId, applicationVersionId, targetUserId, customerId, true, true);
    }

    @When("^User \"([^\"]*)\" (?:removes|deletes) user \"([^\"]*)\" from customer \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userRemovesUserFromCustomer(String requestorName, String targetUserName, UUID customerId, String appVersionName) throws Throwable {
        UUID requestorId = usersSteps.resolveUserId(requestorName);
        UUID targetUserId = usersSteps.resolveUserId(targetUserName);
        UUID applicationVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        customerSteps.removeUserFromCustomerByUserForApp(requestorId, applicationVersionId, customerId, targetUserId);
    }

    @When("^User \"([^\"]*)\" requests (?:list of )?users for property set \"([^\"]*)\"$")
    public void userRequestsListOfUsersForPropertySet(String requestorName, String propertySetName) throws Throwable {
        UUID requestorId = usersSteps.resolveUserId(requestorName);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        propertySetSteps.listOfUsersForPropertySetIsGotByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, null, null, null, null, null);
    }

    @When("^User \"([^\"]*)\" deletes user \"([^\"]*)\" from property \"([^\"]*)\"(?: for application version \"([^\"]*)\")?$")
    public void userDeletesUserFromPropertyForApplicationVersion(String requestorName, String targetUserName, String propertyName, String appVersionName) throws Throwable {
        UUID requestorId = usersSteps.resolveUserId(requestorName);
        UUID targetUserId = usersSteps.resolveUserId(targetUserName);
        UUID propertyId = propertySteps.resolvePropertyId(propertyName);
        UUID appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        propertySteps.deletePropertyUserRelationshipByUserForApp(requestorId, appVersionId, propertyId, targetUserId);
    }

    @When("^Relation between user \"([^\"]*)\" and partner \"([^\"]*)\" is deleted$")
    public void relationBetweenUserAndPartnerIsDeleted(String userName, String partnerName) throws Throwable {
        PartnerDto partner = partnerSteps.getPartnerByName(partnerName);
        assertThat(partner, is(notNullValue()));
        Response response = usersSteps.deleteUserPartnerRelationship(usersSteps.resolveUserId(userName), partner.getId());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_NO_CONTENT));
    }

    @When("^Relation between user \"([^\"]*)\" and partner \"([^\"]*)\" exists$")
    public void relationBetweenUserAndPartnerExists(String userName, String partnerName) throws Throwable {
        usersSteps.userPartnerRelationshipExists(usersSteps.resolveUserId(userName), partnerSteps.resolvePartnerId(partnerName));
    }
}
