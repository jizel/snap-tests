package travel.snapshot.dp.qa.serenity.roles;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.qa.helpers.RoleType.CUSTOMER;
import static travel.snapshot.dp.qa.helpers.RoleType.PROPERTY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleCreateBaseDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.helpers.RoleType;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class RoleBaseSteps extends BasicSteps {

    private static final String SESSION_ROLE_ID = "role_id";
    private static final String SESSION_CREATED_ROLE = "created_role";
    private String roleBasePath = "";
    private static RoleType roleBaseType;


    public RoleBaseSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    public void setRolesPathCustomer() {
        spec.basePath(USER_CUSTOMER_ROLES_PATH);
        roleBasePath = USER_CUSTOMER_ROLES_PATH;
        roleBaseType = CUSTOMER;
    }

    public void setRolesPathProperty() {
        spec.basePath(USER_PROPERTY_ROLES_PATH);
        roleBasePath = USER_PROPERTY_ROLES_PATH;
        roleBaseType = PROPERTY;
    }

    public void setRolesPath(RoleType roleType) {
        switch (roleType) {
            case CUSTOMER: {
                spec.basePath(USER_CUSTOMER_ROLES_PATH);
                roleBasePath = USER_CUSTOMER_ROLES_PATH;
                roleBaseType = CUSTOMER;
            }
            break;
            case PROPERTY: {
                spec.basePath(USER_PROPERTY_ROLES_PATH);
                roleBasePath = USER_PROPERTY_ROLES_PATH;
                roleBaseType = PROPERTY;
            }
            break;
            default:
                fail("Invalid role type given");
        }
    }

    public String getBasePath() {
        return roleBasePath;
    }

    public static RoleType getRoleBaseType() {
        return roleBaseType;
    }


    @Step
    public Response createRole(RoleCreateBaseDto role) {
        setSessionVariable(SESSION_CREATED_ROLE, role);
        Response response = createEntity(role);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void followingRolesExist(List<Map<String, Object>> rolesMap) throws Exception {
        for (Map<String, Object> roleAttributes : rolesMap) {
            RoleBaseDto role = getRoleBaseType().getDtoClassType().newInstance();
            setRoleAttributes(role, roleAttributes);

            if (role.getApplicationId() == null) {
                role.setApplicationId(DEFAULT_SNAPSHOT_APPLICATION_ID);
            }
            try {
                Response createResponse = createRole(role);
                if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                    fail("Role cannot be created " + createResponse.getBody().asString());
                }
            } catch (NullPointerException npe) {
                fail("Cannot create role because role type was not set. Use 'Switch to {User Customer | User Property | User Property Set} role type");
            } catch (Exception ex) {
                fail("Error when preparing role for creating: " + ex.toString());
            }
        }
    }

    @Step
    public void updateRole(UUID roleId, RoleUpdateDto roleUpdate, String etag) {
        try {
            String updatedRoleString = retrieveData(roleUpdate).toString();
            Response response = updateEntity(roleId, updatedRoleString, etag);
            setSessionResponse(response);
        } catch (JsonProcessingException jsonException) {
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public Response getRole(UUID id) {
        Response response = getEntity(id);
        setSessionResponse(response);
        return response;
    }

    public RoleBaseDto getRoleByNameUsingCustomerRole(String name) {
        setRolesPathCustomer();
        return getRoleByName(name);
    }

    public RoleBaseDto getRoleByName(String name) {
        String filter = String.format("name=='%s'", name);
        Response response = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null);
        List<RoleBaseDto> roles = getResponseAsRoles(response);
        return roles.get(0);
    }

    @Step
    public void getRoleWithNameForApplicationId(String name, UUID applicationId) {
        //TODO implement actual customer search
        RoleBaseDto roleByName = getRoleByName(name);

        Response resp = getRole(roleByName.getId());
        setSessionResponse(resp);
    }

    @Step
    public void deleteRole(UUID roleId) {
        deleteEntityWithEtag(roleId);
        setSessionVariable(SESSION_ROLE_ID, roleId);
    }

    @Step
    public void roleIdInSessionDoesntExist() {
        UUID roleId = Serenity.sessionVariableCalled(SESSION_ROLE_ID);

        Response response = getRole(roleId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void listOfRolesIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void deleteRoles(List<RoleBaseDto> roles) {
        roles.forEach(r -> {
            RoleBaseDto existingRole = getRoleByName(r.getName());
            if (existingRole != null) {
                deleteRole(existingRole.getId());
            }
        });
    }

    public void roleNamesAreInResponseInOrder(List<String> names) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        List<RoleBaseDto> roles = getResponseAsRoles(response);
        int i = 0;
        for (RoleBaseDto role : roles) {
            assertEquals("Role on index=" + i + " is not expected", names.get(i), role.getName());
            i++;
        }
    }

    public static List<RoleBaseDto> getResponseAsRoles(Response response) {
        RoleBaseDto[] roles = null;
        switch (getRoleBaseType()) {
            case CUSTOMER:
                roles = response.as(CustomerRoleDto[].class);
                break;
            case PROPERTY:
                roles = response.as(PropertyRoleDto[].class);
                break;
        }
        return Arrays.asList(roles);
    }

    public void compareRoleOnHeaderWithStored(String headerName) throws Exception {
        RoleDto originalRole = Serenity.sessionVariableCalled(SESSION_CREATED_ROLE);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String roleLocation = response.header(headerName).replaceFirst(getBasePath(), "");
        given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .get(roleLocation).then()
                .body("application_id", is(originalRole.getApplicationId()))
                .body("description", is(originalRole.getDescription()))
                .body("name", is(originalRole.getName()));
    }

    public UUID resolveRoleId(String roleName) {
        UUID roleId;
        if (isUUID(roleName)) {
            roleId = UUID.fromString(roleName);
        } else {
            RoleBaseDto role = getRoleByName(roleName);
            assertThat(String.format("Role with name \"%s\" does not exist", roleName), role, is(notNullValue()));
            roleId = role.getId();
        }
        return roleId;
    }

    public RoleBaseDto setRoleAttributes(RoleBaseDto roleDto, Map<String, Object> roleAttributes) {
        roleDto.setId(UUID.fromString(Objects.toString(roleAttributes.get("id"), null)));
        roleDto.setDescription(Objects.toString(roleAttributes.get("description")));
        roleDto.setApplicationId(UUID.fromString(Objects.toString(roleAttributes.get("applicationId"), null)));
        roleDto.setName(Objects.toString(roleAttributes.get("roleName"), null));
        if (roleAttributes.containsKey("isActive")) {
            roleDto.setIsActive(Boolean.valueOf(Objects.toString(roleAttributes.get("isActive"))));
        }
        if (roleAttributes.containsKey("isInitial")) {
            roleDto.setIsInitial(Boolean.valueOf(Objects.toString(roleAttributes.get("isInitial"))));
        }
        return roleDto;
    }
}
