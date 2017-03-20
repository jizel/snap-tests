package travel.snapshot.dp.qa.serenity.users;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersSteps extends BasicSteps {

    private static final String SESSION_USER_ID = "user_id";
    private static final String SESSION_CREATED_USER = "created_user";

    private static final String USERS_PATH = "/identity/users";

    public UsersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USERS_PATH);
    }

    public void followingUsersExist(List<UserCreateDto> users, String customerId, Boolean isPrimary, Boolean isActive) {
        users.forEach(user -> {
            UserCustomerRelationshipDto relation = new UserCustomerRelationshipDto();
            relation.setCustomerId(customerId);
            relation.setIsPrimary(isPrimary);
            relation.setIsActive(isActive);
            user.setUserCustomerRelationship(relation);

            Response createResp = createEntity(user);
            if (createResp.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("User cannot be created! Status:" + createResp.getStatusCode() + " " + createResp.getBody().asString());
            }
        });
    }

    @Step
    public void createUserWithCustomer(UserCreateDto user, String customerId, Boolean isPrimary) {
        UserCustomerRelationshipDto relation = new UserCustomerRelationshipDto();
        relation.setCustomerId(customerId);
        relation.setIsPrimary(isPrimary);
        user.setUserCustomerRelationship(relation);

        Response response = createEntity(user);
        setSessionResponse(response);
    }

    @Step
    public void createUser(UserCreateDto user) {
        Response response = createEntity(user);
        setSessionResponse(response);
    }

    @Step
    public void createUserByUser(String requestorID, UserCreateDto user) {
        setSessionResponse(createEntityByUser(requestorID, user));
    }

    @Step
    public void bodyContainsUserWith(String attributeName, String value) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(value));

        String userId = response.getBody().jsonPath().getString("user_id");
        setSessionVariable(SESSION_USER_ID, userId);
    }

    @Step
    public void compareUserOnHeaderWithStored(String headerName) {
        UserDto originalUser = getSessionVariable(SESSION_CREATED_USER);
        Response response = getSessionResponse();
        String customerLocation = response.header(headerName).replaceFirst(USERS_PATH, "");
        given().spec(spec).get(customerLocation).then()
                .body("user_name", is(originalUser.getUserName()))
                .body("first_name", is(originalUser.getFirstName()))
                .body("last_name", is(originalUser.getLastName()))
                .body("email", is(originalUser.getEmail()))
                .body("phone", is(originalUser.getPhone()))
                .body("culture", is(originalUser.getCulture()))
                .body("timezone", is(originalUser.getTimezone()));

    }

    @Step
    public void deleteUser(String userId) {
        String etag = getEntityEtag(userId);
        deleteUserWithEtag(userId, etag);
    }

    @Step
    public void deleteUserWithEtag(String userId, String etag) {
        Response response = deleteEntity(userId, etag);
        setSessionResponse(response);
    }

    @Step
    public void userIdInSessionDoesNotExist() {
        String roleId = getSessionVariable(SESSION_USER_ID);

        Response response = getEntity(roleId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

//    Delete?

    @Step
    public void updateUser(String userId, UserUpdateDto updatedUser) throws Throwable {
        updateUserByUser(DEFAULT_SNAPSHOT_USER_ID, userId, updatedUser);
    }

    @Step
    public void updateUserByUser(String performerId, String targetId, UserUpdateDto updatedUser) throws Throwable {
        try {
            Response originalResponse = getEntity( targetId );
            JSONObject userData = retrieveData( updatedUser );
            Response response = updateEntityByUser( performerId, targetId, userData.toString(), originalResponse.getHeader( HEADER_ETAG ) );
            setSessionResponse( response );
        } catch(JsonProcessingException jsonException) {
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public void userWithUserNameHasData(String userName, UserDto user) throws Throwable {
        Map<String, Object> originalData = retrieveDataOld(UserDto.class, getUserByUsername(userName));
        Map<String, Object> expectedData = retrieveDataOld(UserDto.class, user);

        expectedData.forEach((k, v) -> {
            if (v == null) {
                assertFalse("User JSON should not contains attributes with null values", originalData.containsKey(k));
                return;
            }
            assertTrue("User has no data for attribute " + k, originalData.containsKey(k));
            assertEquals(v, originalData.get(k));
        });
    }

    @Step
    public void updateUserWithUserNameIfUpdatedBefore(String userName, UserDto updatedUser) throws Throwable {
        UserDto original = getUserByUsername(userName);

        Map<String, Object> userData = retrieveDataOld(UserDto.class, updatedUser);

        Response response = updateEntity(original.getId(), userData, "fake-etag");
        setSessionResponse(response);
    }


    public UserDto getUserByUsername(String username) {
        UserDto[] users = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + username, null, null, null).as(UserDto[].class);
        return Arrays.asList(users).stream().findFirst().orElse(null);
    }

    @Step
    public void userWithUsernameIsGot(String username) {
        UserDto user = getUserByUsername(username);
        Response response = getEntity(user.getId());
        setSessionResponse(response);
    }

    public void userWithUsernameIsGotWithEtag(String username) {
        UserDto user = getUserByUsername(username);
        Response tempResponse = getEntity(user.getId());
        Response response = getEntity(user.getId());
        setSessionResponse(response);
    }

    public void userWithIdIsGot(String userId) {
        Response response = getEntity(userId);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotByUser(String requestorId, String limit, String cursor, String filter, String sort, String sortDesc) {
        listOfUsersIsGotByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, limit, cursor, filter, sort, sortDesc);
    }

    public void listOfUsersIsGotByUserForApp(String requestorId, String appVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(requestorId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        UserDto[] users = response.as(UserDto[].class);
        int i = 0;
        for (UserDto u : users) {
            assertEquals("User on index=" + i + " is not expected", usernames.get(i), u.getUserName());
            i++;
        }
    }


    public void roleIsAddedToUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);

        Response response = addRoleToUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        setSessionResponse(response);
    }

    public void relationExistsBetweenRoleAndUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);

        RoleDto existingUserRole = getRoleForUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        if (existingUserRole != null) {

            Response deleteResponse = deleteRoleFromUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertyUser cannot be deleted - status: " + deleteResponse.getStatusCode() + ", " + deleteResponse.asString());
            }
        }
        Response createResponse = addRoleToUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("PropertyUser cannot be created - status: " + createResponse.getStatusCode() + ", " + createResponse.asString());
        }
    }

    @Step
    public void roleExistsBetweenUserAndEntity(String entityName, String roleId, String userName, String entityId) {
        RoleDto existingUserEntityRole = getRoleForUserEntity(roleId, userName, entityId, entityName);
        if (existingUserEntityRole != null) {
            Response deleteResponse = deleteEntityUrl(buildPathForRoles(entityName, userName, entityId), roleId);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertyUser cannot be deleted - status: " + deleteResponse.getStatusCode() + ", " + deleteResponse.asString());
            }
        }
        Response createResponse = addRoleToUserEntity(roleId, userName, entityId, entityName);
        setSessionResponse(createResponse);
    }

    private Response deleteRoleFromUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        return deleteSecondLevelEntity(userId, SECOND_LEVEL_OBJECT_ROLES, roleId, queryParams);
    }

    private Response addRoleToUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> data = new HashMap<>();
        data.put("relationship_type", relationshipType);
        data.put("relationship_id", entityId);
        data.put("role_id", roleId);

        return given().spec(spec)
                .body(data)
                .when().post("/{userId}/roles", userId);
    }

    private Response addRoleToUserEntity(String roleId, String userName, String entityId, String entityName) {
        Map<String, String> data = new HashMap<>();
        data.put("role_id", roleId);

        String path = buildPathForRoles(entityName, userName, entityId);
        return given().spec(spec)
                .body(data)
                .when().post(path);
    }

    private RoleDto getRoleForUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        Response userRolesResponse = getSecondLevelEntities(userId, SECOND_LEVEL_OBJECT_ROLES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "role_id==" + roleId, null, null, queryParams);
        return Arrays.asList(userRolesResponse.as(RoleDto[].class)).stream().findFirst().orElse(null);
    }

    private RoleDto getRoleForUserEntity(String roleId, String userName, String entityId, String entityName) {
        String path = buildPathForRoles(entityName, userName, entityId);
        Response userRolesUserEntity = getEntities(path, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "role_id==" + roleId, null, null, null);
        return Arrays.asList(userRolesUserEntity.as(RoleDto[].class)).stream().findFirst().orElse(null);
    }

    public void roleIsDeletedFromUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);
        Response deleteResponse = deleteRoleFromUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        setSessionResponse(deleteResponse);
    }

    public void roleDoesntExistForUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);
        RoleDto existingUserRole = getRoleForUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        assertNull("Role should not be present for User", existingUserRole);
    }

    public void listOfRolesIsGotForRelationshipTypeEntityWIth(String username, String relationshipType, String entityId, String limit, String cursor, String filter, String sort, String sortDesc) {
        UserDto u = getUserByUsername(username);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        Response response = getSecondLevelEntities(u.getId(), SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc, queryParams);
        setSessionResponse(response);
    }

    public void rolenamesAreInResponseInOrder(List<String> rolenames) {
        Response response = getSessionResponse();
        RoleDto[] userRoles = response.as(RoleDto[].class);
        int i = 0;
        for (RoleDto ur : userRoles) {
            assertEquals("UserRole on index=" + i + " is not expected", rolenames.get(i), ur.getRoleName());
            i++;
        }
    }

    public void setUserPasswordByUsername(String username, String password) {
        UserDto u = getUserByUsername(username);
        setUserPassword(u.getId(), password);
    }

    public void setUserPassword(String id, String password) {
        given().spec(spec).body(password).post("{id}/password", id);
    }

    @Step
    public void setUserIsActive(String userId, Boolean isActive) throws Throwable {
        UserUpdateDto userUpdate = new UserCreateDto();
        userUpdate.setIsActive(isActive);
        updateUser(userId, userUpdate);
    }

    @Step
    public Boolean getUserIsActive(String userId){
        return getUserById(userId).getIsActive();
    }

    @Step
    public UserDto getUserById(String userId) {
        Response response = getEntity(userId);
        UserDto user = response.as(UserDto.class);
        setSessionResponse(response);
        return user;
    }

    @Step
    public void roleExistsBetweenNotExistingUserAndEntity(String entityName, String roleId, String userId, String entityId) {
        Map<String, String> data = new HashMap<>();
        data.put("role_id", roleId);

        Response resp = given().spec(spec).body(data).when()
                .post(String.format("%s/%s/%s/%s", userId, entityName, entityId, SECOND_LEVEL_OBJECT_ROLES));

        setSessionResponse(resp);
    }

    @Step
    public void roleBetweenUserAndEntityIsDeleted(String entityName, String roleId, String userName, String entityId) {
        Response deleteResponse = deleteEntityUrl(buildPathForRoles(entityName, userName, entityId), roleId);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void roleBetweenUserAndEntityNotExists(String entityName, String roleId, String userName, String entityId) {
        RoleDto roleDto = getRoleForUserEntity(roleId, userName, entityId, entityName);
        if (roleDto != null) {
            fail("Role exists and should not ! Role id = " + roleId);
        }
    }

    @Step
    public void roleNameExistsBetweenUserAndEntity(String entityName, String roleId, String userName, String entityId) {
        Response resp = addRoleToUserEntity(roleId, userName, entityId, entityName);
        setSessionResponse(resp);
    }

    @Step
    public void getRolesBetweenUserAndEntity(String entityName, String userName, String entityId, String limit, String cursor, String filter, String sort, String sortDesc) {
        String path = buildPathForRoles(entityName, userName, entityId);
        Response resp = getEntities(path, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(resp);
    }

    @Step
    public void updateUserPropertyRelationship(String userId, String propertyId, UserPropertyRelationshipUpdateDto userPropertyRelationship) {
        updateUserPropertyRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertyId, userPropertyRelationship);
    }

    @Step
    public void updateUserPropertyRelationshipByUser(String performerId, String userId, String propertyId, UserPropertyRelationshipUpdateDto userPropertyRelationship) {
        try {
            JSONObject jsonUpdate = retrieveData(userPropertyRelationship);
            jsonUpdate.remove(SESSION_USER_ID);
            String etag = getSecondLevelEntityEtag(userId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId);
            Response response = updateSecondLevelEntityByUser(performerId, userId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserPropertyRelationshipUpdateDto object");
        }
    }

    @Step
    public Response addPropertySetToUser(String propertySetId, String userId){
        Response response =  addPropertySetToUserByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, userId);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addPropertySetToUserByUser(String performerId, String propertySetId, String userId){
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, performerId)
                .body(singletonMap("property_set_id", propertySetId))
                .when().post("/{userId}/property_sets", userId);
    }

    private String buildPathForRoles(String entityName, String userName, String entityId) {
        UserDto user = getUserByUsername(userName);
        if (user == null) {
            return String.format("%s/%s/%s/%s", userName, entityName, entityId, SECOND_LEVEL_OBJECT_ROLES);
        }
        return String.format("%s/%s/%s/%s", user.getId(), entityName, entityId, SECOND_LEVEL_OBJECT_ROLES);
    }

    public void createUserForCustomerByUser(String performerId, String customerId, UserCreateDto user, Boolean isPrimary) {
        UserCustomerRelationshipDto relation = new UserCustomerRelationshipDto();
        relation.setCustomerId(customerId);
        relation.setIsPrimary(isPrimary);
        user.setUserCustomerRelationship(relation);
        Response createResp = createEntityByUser(performerId, user);
        setSessionResponse(createResp);
    }

    public void deleteUserByUser(String performerId, String targetId) {
        String etag = getEntityEtag(targetId);
        deleteEntityByUser( performerId, targetId, etag);
    }

    public void getUserCustomerRelationByUserForApp(String requestorId, String appVersionId, String customerId, String targetUserId) {
        Response response = getSecondLevelEntityByUserForApp(requestorId, appVersionId, targetUserId, SECOND_LEVEL_OBJECT_CUSTOMERS, customerId);
        setSessionResponse(response);
    }

    public void listUserCustomersByUser(String requestorId, String targetUserId) {
        Response response = getSecondLevelEntitiesByUser(requestorId, targetUserId, SECOND_LEVEL_OBJECT_CUSTOMERS, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public void listRolesForRelationByUserForApp(String requestorId,  String appVersionId, String targetUserId, String secondLevelName, String secondLevelId) {
        Response response = getThirdLevelEntitiesByUserForApp(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, SECOND_LEVEL_OBJECT_ROLES, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public String resolveUserId(String userName) {
        if (userName == null) return DEFAULT_SNAPSHOT_USER_ID;

        String userId;
        if (isUUID(userName)) {
            userId = userName;
        } else {
            UserDto user = getUserByUsername(userName);
            assertThat(String.format("User with username \"%s\" does not exist", userName), user, is(notNullValue()));
            userId = user.getId();
        }
        return userId;
    }

    public Map<String, String> getUsersIds(String requestorName, String targetName) {
        Map<String, String> userIdMap = new HashMap<String, String>();
        userIdMap.put(REQUESTOR_ID, resolveUserId(requestorName));
        userIdMap.put(TARGET_ID, resolveUserId(targetName));
        return userIdMap;
    }

    public void userAssignsRoleToRelationWithApp(String requestorId, String appVersionId, String targetUserId, String secondLevelName, String secondLevelId, String roleId) {
        Response response = createThirdLevelEntityByUserForApplication(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, SECOND_LEVEL_OBJECT_ROLES, singletonMap(ROLE_ID, roleId));
        setSessionResponse(response);
    }

    public void userDeletesRoleFromRelationWithApp(String requestorId, String appVersionId, String targetUserId, String secondLevelName, String secondLevelId, String roleId) {
        String eTag = getThirdLevelEntityEtag(targetUserId, resolveObjectName(secondLevelName), secondLevelId, SECOND_LEVEL_OBJECT_ROLES, roleId);
        Response response = deleteThirdLevelEntityByUserForApplication(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, SECOND_LEVEL_OBJECT_ROLES, roleId, eTag);
        setSessionResponse(response);
    }

    @Step
    public UserCustomerRelationshipDto getCustomerForUser(String userId, String customerId) {
        Response userCustomerResponse = getSecondLevelEntities(userId, SECOND_LEVEL_OBJECT_CUSTOMERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + customerId, null, null, null);
        return stream(userCustomerResponse.as(UserCustomerRelationshipDto[].class)).findFirst().orElse(null);
    }
}
