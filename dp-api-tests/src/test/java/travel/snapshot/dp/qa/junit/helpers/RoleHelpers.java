package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.qa.cucumber.helpers.RoleType;
import travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps;

/**
 * Created by zelezny on 6/26/2017.
 */
public class RoleHelpers extends RoleBaseSteps {

    public RoleDto roleIsCreated(RoleDto role, RoleType roleType) {
        setRolesPath(roleType);
        Response response = createRole(role);
        assertEquals(String.format("Failed to create role: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(roleType.getDtoClassType());
    }
}
