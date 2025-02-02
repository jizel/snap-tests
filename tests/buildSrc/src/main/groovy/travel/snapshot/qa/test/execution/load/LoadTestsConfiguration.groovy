package travel.snapshot.qa.test.execution.load

import travel.snapshot.qa.util.Properties

class LoadTestsConfiguration {

    static final int DEFAULT_START_USERS = 10

    static final int DEFAULT_END_USERS = 60

    static final int DEFAUT_RAMP = 120

    private LoadTestEnvironment environment

    private int startUsers = DEFAULT_START_USERS

    private int endUsers = DEFAULT_END_USERS

    private int ramp = DEFAUT_RAMP

    private OAuthConfiguration oauthConfiguration

    static LoadTestsConfiguration parseLoadTestsConfiguration() {

        LoadTestsConfiguration configuration = new LoadTestsConfiguration()
                .environment(Properties.LoadTest.environment)
                .startUsers(Properties.LoadTest.startUsers)
                .endUsers(Properties.LoadTest.endUsers)
                .ramp(Properties.LoadTest.ramp)
                .oauthConfiguration(Properties.LoadTest.oauthConfiguration)

        configuration
    }

    static List<List<?>> parse() {

        LoadTestsConfiguration configuration = parseLoadTestsConfiguration()

        List<List<?>> configurations = []

        for (LoadTestsSimulation simulation : Properties.LoadTest.simulations) {
            configurations << [simulation, configuration]
        }

        configurations
    }

    LoadTestsConfiguration environment(LoadTestEnvironment environment) {
        this.environment = environment
        this
    }

    LoadTestsConfiguration startUsers(int startUsers) {
        this.startUsers = startUsers
        this
    }

    LoadTestsConfiguration endUsers(int endUsers) {
        this.endUsers = endUsers
        this
    }

    LoadTestsConfiguration ramp(int ramp) {
        this.ramp = ramp
        this
    }

    LoadTestsConfiguration oauthConfiguration(OAuthConfiguration oauthConfiguration) {
        this.oauthConfiguration = oauthConfiguration
        this
    }

    LoadTestEnvironment getEnvironment() {
        environment
    }

    int getStartUsers() {
        startUsers
    }

    int getEndUsers() {
        endUsers
    }

    int getRamp() {
        ramp
    }

    String getHost() {
        host
    }

    OAuthConfiguration getOAuthConfiguration() {
        oauthConfiguration
    }

    @Override
    String toString() {
        "environment: ${environment}, startUsers: ${startUsers}, endUsers: ${endUsers}, ramp: ${ramp}"
    }
}
