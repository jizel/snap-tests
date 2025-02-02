package travel.snapshot.dp.qa.helpers;

import static travel.snapshot.dp.api.identity.model.CustomerType.HOTEL;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_CUSTOMER_TYPE;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_SALESFORCE_ID;

import org.apache.commons.lang3.RandomStringUtils;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.type.SalesforceId;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by vlcek on 5/26/2016.
 */
public class CustomerUtils {

    public static CustomerCreateDto createRandomCustomer() {
        return createRandomCustomer(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(2),
                RandomStringUtils.randomAlphabetic(10),
                "CZ" + RandomStringUtils.randomNumeric(8),
                Boolean.TRUE,
                "+420" + RandomStringUtils.randomNumeric(9),
                "http://www.snapshot.travel",
                "Europe/Prague",
                HOTEL
        );
    }

    public static CustomerCreateDto createRandomCustomer(String vatId) {
        return createRandomCustomer(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(5).toLowerCase() + "@" + RandomStringUtils.randomAlphabetic(5).toLowerCase() + "." + RandomStringUtils.randomAlphabetic(2).toLowerCase(),
                DEFAULT_SNAPSHOT_SALESFORCE_ID,
                vatId,
                Boolean.TRUE,
                "+420" + RandomStringUtils.randomNumeric(9),
                "http://www.snapshot.travel",
                "Europe/Prague",
                HOTEL
        );
    }

    public static CustomerCreateDto createRandomCustomer(String companyName, String email, String salesForce, String vatId, Boolean demoCustomer, String phone, String website, String timezone, CustomerType type) {
        CustomerCreateDto customer = new CustomerCreateDto();
        customer.setName(companyName);
        customer.setEmail(email);
        customer.setSalesforceId(SalesforceId.of(salesForce));
        customer.setVatId(vatId);
        customer.setIsDemo(demoCustomer);
        customer.setPhone(phone);
        customer.setWebsite(website);
        customer.setTimezone(timezone);
        customer.setType(type);
        return customer;
    }

    public static CustomerCreateDto createCustomerDto(Map<String, Object> customerAttributes){
        CustomerCreateDto customerDto = new CustomerCreateDto();
        customerDto.setId(UUID.fromString(Objects.toString(customerAttributes.get("id"), null)));
        customerDto.setWebsite(Objects.toString(customerAttributes.get("website"), null));
        customerDto.setVatId(Objects.toString(customerAttributes.get("vatId"), null));
        customerDto.setEmail(Objects.toString(customerAttributes.get("email"), null));
        customerDto.setIsDemo(Boolean.valueOf(Objects.toString(customerAttributes.get("id"), null)));
        customerDto.setNotes(Objects.toString(customerAttributes.get("notes"), null));
        customerDto.setPhone(Objects.toString(customerAttributes.get("phone"), null));
        customerDto.setTimezone(Objects.toString(customerAttributes.get("timezone"), null));
        customerDto.setName(Objects.toString(customerAttributes.get("name"), null));
        customerDto.setParentId(UUID.fromString(Objects.toString(customerAttributes.get("parentId"), null)));
        customerDto.setSalesforceId(SalesforceId.of(Objects.toString(customerAttributes.get("salesforceId"), null)));
        if(customerAttributes.containsKey("isActive")){
            customerDto.setIsActive(Boolean.valueOf((customerAttributes.get("isActive")).toString()));
        }
        if (customerAttributes.containsKey("type")) {
            customerDto.setType(CustomerType.valueOf(Objects.toString(customerAttributes.get("type"), null).toUpperCase()));
        } else {
            customerDto.setType(CustomerType.valueOf(DEFAULT_CUSTOMER_TYPE));
        }
        return customerDto;
    }

}
