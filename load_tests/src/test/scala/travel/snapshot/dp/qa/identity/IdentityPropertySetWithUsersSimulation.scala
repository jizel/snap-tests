package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityPropertySetWithUsersSimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their property sets with assigned users").exec(
    CreateCustomer(),
    pause(1),
    CreateAndAssignUsersToCustomer(),
    pause(1),
    CreateAndAssignPropertySetsToCustomerAndAssignUser(10),
    pause(1),
    GetCustomersPropertySets()))
}
