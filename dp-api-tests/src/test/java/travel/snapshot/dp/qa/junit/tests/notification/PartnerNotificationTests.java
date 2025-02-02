package travel.snapshot.dp.qa.junit.tests.notification;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Partner entity
 */
public class PartnerNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/partner_notification_tests.yaml"));
    private Map<String, Object> receivedNotification;
    private UUID createdPartnerId;

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPartnerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPartnerNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(testPartner1);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePartnerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePartnerNotificationTest");
        createdPartnerId = entityIsCreated(testPartner1);
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setName("Updated partner name");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsUpdated(PARTNERS_PATH, createdPartnerId, partnerUpdate) ;
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePartnerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePartnerNotificationTest");
        createdPartnerId = entityIsCreated(testPartner1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsDeleted(PARTNERS_PATH, createdPartnerId);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void CreateUpdatePartnerByCustomerUserNotificationTest() throws Exception{
        UUID createdUserId = entityIsCreated(testUser1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);

        createEntityByUserForApplication(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,  testPartner2).then().statusCode(SC_CREATED);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(getSingleTestData(notificationTestsData, "createPartnerByCustomerUserNotificationTest"), receivedNotification);

        PartnerUpdateDto PartnerUpdateDto = new PartnerUpdateDto();
        PartnerUpdateDto.setName("Updated Partner Name");
        updateEntityByUserForApp(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PARTNERS_PATH, DEFAULT_SNAPSHOT_PARTNER_ID, PartnerUpdateDto).then().statusCode(SC_OK);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(getSingleTestData(notificationTestsData, "updatePartnerByCustomerUserNotificationTest"), receivedNotification);
    }

}
