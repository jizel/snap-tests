package travel.snapshot.dp.qa.serenity.users;

import static com.jayway.awaitility.Awaitility.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.assertj.core.api.Fail.fail;
import static org.assertj.core.api.Fail.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.junit.Assert.assertThat;

import com.jayway.restassured.response.Response;
import static org.hamcrest.Matchers.is;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by benka on 05-May-16.
 */
public class UserRolesSteps extends UsersSteps {


    public void roleExistsBetweenUserAndCustomer(String roleId, String userId, String customerId) {
        Response response = createRoleBetweenUserAndCustomer(roleId, userId, customerId);
        assertThat("Failed to assign role: " + response.body().toString(), response.statusCode(), is(SC_CREATED));
    }

    public Response createRoleBetweenUserAndCustomer(String roleId, String userId, String customerId) {
        return createRoleBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userId, customerId);
    }

    public void roleBetweenUserAndCustomerIsDeleted(String roleId, String userId, String customerId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userId, customerId);
    }

    public void roleBetweenUserAndCustomerNotExists(String roleId, String userName, String customerId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void roleNameExistsBetweenUserAndCustomer(String roleId, String userName, String customerId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void getRolesBetweenUserAndCustomer(String userName, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, userName, customerId, limit, cursor, filter, sort, sortDesc);
    }

//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndProperty(String roleId, String userName, String propId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propId);
    }

    public void addRoleBetweenNotExistingUserAndProperty(String roleId, String s, String customerId) {
        roleExistsBetweenNotExistingUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, s, customerId);
    }

    public void roleBetweenUserAndPropertyIsDeleted(String roleId, String userId, String propertyId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userId, propertyId);
    }

    public void roleBetweenUserAndPropertyNotExists(String roleId, String userName, String propertyId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void roleNameExistsBetweenUserAndProperty(String roleId, String userName, String propertyId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void getRolesBetweenUserAndProperty(String userName, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, userName, propertyId, limit, cursor, filter, sort, sortDesc);
    }


//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleBetweenUserAndPropertySetIsDeleted(String roleId, String userId, String propertySetId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userId, propertySetId);
    }

    public void roleBetweenUserAndPropertySetNotExists(String roleId, String userName, String propertySetId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleNameExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void getRolesBetweenUserAndPropertySet(String userName, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, userName, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void addRoleBetweenNotExistingUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }
}
