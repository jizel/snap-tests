package travel.snapshot.dp.qa.junit.tests.identity.users;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;

import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Tests for user type based restrictions
 */
public class userPasswordRestrictionTests extends CommonTest {

    @Jira("DP-1985")
    @Test
    public void userPasswordRestrictionTest() throws Exception {
        UserDto customerUser1 = entityIsCreatedAs(UserDto.class, testUser1);
        UserDto customerUser2 = entityIsCreatedAs(UserDto.class, testUser2);
        UserDto partnerUser = entityIsCreatedAs(UserDto.class, testUser3);
//        Valid cases - snapshot user and user himself
        userHelpers.setUserPassword(customerUser1.getId(), "newPassword");
        responseCodeIs(SC_NO_CONTENT);
        userHelpers.setUserPasswordByUser(customerUser1.getId(), customerUser1.getId(), "NewPasswordSetByUserHimself");
        responseCodeIs(SC_NO_CONTENT);
//        Invalid cases - other users
        userHelpers.setUserPasswordByUser(customerUser2.getId(), customerUser1.getId(), "otherCustomerUserCannotSetNewPassword");
        responseCodeIs(SC_FORBIDDEN);
        customCodeIs(CC_INSUFFICIENT_PERMISSIONS);
        userHelpers.setUserPasswordByUser(partnerUser.getId(), customerUser1.getId(), "otherCustomerUserCannotSetNewPassword");
        responseCodeIs(SC_FORBIDDEN);
        customCodeIs(CC_INSUFFICIENT_PERMISSIONS);
    }
}
