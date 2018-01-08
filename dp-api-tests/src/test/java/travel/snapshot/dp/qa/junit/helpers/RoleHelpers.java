package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleCreateBaseDto;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.cucumber.helpers.RoleType;
import travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for roles. Obsolete dependency on RoleBaseSteps will be removed when obsolete role types are removed completely from IM.
 */
public class RoleHelpers extends RoleBaseSteps {

//    TODO: Delete this reference once ATM-53 is done
    private CommonHelpers commonHelpers = new CommonHelpers();
//    Remove when old role endpoints are removed from IM
    private UsersSteps usersSteps = new UsersSteps();
    private RoleBaseSteps roleBaseSteps = new RoleBaseSteps();

    protected final RelationshipsHelpers relationshipsHelpers = new RelationshipsHelpers();

    public static RoleCreateDto constructRole(UUID applicationId, String name) {
        RoleCreateDto role = new RoleCreateDto();
        role.setApplicationId(applicationId);
        role.setIsActive(true);
        role.setName(name);
        role.setIsInitial(true);
        return role;
    }

    @Deprecated
    public RoleBaseDto roleIsCreated(RoleCreateBaseDto role, RoleType roleType) {
        roleBaseSteps.setRolesPath(roleType);
        Response response = roleBaseSteps.createRole(role);
        assertEquals(String.format("Failed to create role: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(roleType.getDtoClassType());
    }

    public void setRoleIsActive(UUID roleId, boolean isActive){
        RoleUpdateDto roleUpdateDto = new RoleUpdateDto();
        roleUpdateDto.setIsActive(isActive);
        entityIsUpdated(ROLES_PATH, roleId, roleUpdateDto);
    }

    /**
     * Remove when old role endpoints are removed from IM
     */
    @Deprecated
    public Response assignRoleToUserCustomerRelationshipOld(UUID userId, UUID customerId, RoleRelationshipDto roleRelationship){
        return usersSteps.createThirdLevelEntity(userId, CUSTOMERS_RESOURCE, customerId, ROLES_RESOURCE, roleRelationship)
                .then()
                .statusCode(SC_CREATED)
                .extract()
                .response();
    }
}
