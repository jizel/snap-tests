package travel.snapshot.dp.qa.junit.tests.identity_smoke;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Property entity
 */
public class PropertyNotificationSmokeTests extends CommonSmokeTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private UUID createdCustomerId;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() {
        super.setUp();
        createdCustomerId = authorizationHelpers.entityIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPropertyNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        authorizationHelpers.entityIsCreated(testProperty2);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }
}
