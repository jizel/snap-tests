package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTest;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JMS notification tests for Property entity
 */
@RunWith(SerenityRunner.class)
public class PropertyNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private PropertyDto testProperty1;
    private PropertyDto testProperty2;
    private CustomerDto testCustomer1;
    Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        testCustomer1 = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        testProperty1 = propertyHelpers.propertyIsCreated(entitiesLoader.getPropertyDtos().get("property1"));
        testProperty2 = entitiesLoader.getPropertyDtos().get("property2");
    }

    @After
    public void cleanUp() throws Exception{
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPropertyNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.propertyIsCreated(testProperty2);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//    DP-1728
    @Test
    public void createPropertyByCustomerUserNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyByCustomerUserNotificationTest");
        UserDto customerUser = userHelpers.userWithCustomerIsCreated(entitiesLoader.getUserDtos().get("user1"), testCustomer1.getId());
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.propertyIsCreatedByUser(customerUser.getId(), testProperty2);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyNotificationTest");
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Update Property Name");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySteps.updateProperty(testProperty1.getId(), propertyUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePropertyNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySteps.deleteProperty(testProperty1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    @Ignore
    public void addRemovePropertyUserNotificationTest() throws Exception{
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "addRemovePropertyUserNotificationTest");
        Map<String, Object> expectedCreateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedCreateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        UserDto testUser = userHelpers.userIsCreated(entitiesLoader.getSnapshotUserDtos().get("snapshotUser1"));
//        Subscribe and test
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySteps.addUserToProperty(testUser.getId(), testProperty1.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        propertySteps.userIsDeletedFromProperty(testUser.getId(), testProperty1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    @Ignore
    public void updateRemovePropertyCustomerNotificationTest() throws Exception{
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "updateRemovePropertyCustomerNotificationTest");
        Map<String, Object> expectedUpdateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedUpdateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        CustomerPropertyRelationshipPartialUpdateDto customerPropertyUpdate = new CustomerPropertyRelationshipPartialUpdateDto();
        customerPropertyUpdate.setType(OWNER);
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(testProperty1.getId(), testCustomer1.getId(), null, null, null, true);
//        Subscribe and test
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySteps.updatePropertyCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                testProperty1.getId(), testCustomer1.getId(), customerPropertyUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        propertySteps.deletePropertyCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                testProperty1.getId(), testCustomer1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
