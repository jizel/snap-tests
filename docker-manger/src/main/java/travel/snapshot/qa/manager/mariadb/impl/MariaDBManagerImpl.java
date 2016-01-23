package travel.snapshot.qa.manager.mariadb.impl;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import org.flywaydb.core.Flyway;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManagerException;
import travel.snapshot.qa.manager.mariadb.check.MariaDBStartedCheckTask;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MariaDBManagerImpl implements MariaDBManager {

    private static final Logger logger = Logger.getLogger(MariaDBManagerImpl.class.getName());

    private final MariaDBManagerConfiguration configuration;

    public MariaDBManagerImpl() {
        this.configuration = new MariaDBManagerConfiguration.Builder().build();
    }

    public MariaDBManagerImpl(final MariaDBManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void waitForConnectivity() {
        Spacelift.task(configuration, MariaDBStartedCheckTask.class).execute().until(
                new CountDownWatch(configuration.getStartupTimeoutInSeconds(), TimeUnit.SECONDS),
                new BasicWaitingCondition());
    }

    @Override
    public void executeScript(final Reader reader) {
        Connection connection = getConnection();
        executeScript(connection, reader);
        closeConnection(connection);
    }

    @Override
    public void executeScript(String sqlScript) {
        executeScript(new File(sqlScript));
    }

    @Override
    public void executeScript(File sqlScript) {
        try (final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlScript)))) {
            executeScript(reader);
        } catch (IOException ex) {
            throw new MariaDBManagerException(String.format("Unable to find SQL script file to execute: %s. ",
                    sqlScript.getAbsolutePath()), ex);
        }
    }

    @Override
    public void executeScript(Connection connection, String sqlScript) {
        executeScript(connection, new File(sqlScript));
    }

    @Override
    public void executeScript(Connection connection, File sqlScript) {
        try (final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlScript)))) {
            executeScript(connection, reader);
        } catch (IOException ex) {
            throw new MariaDBManagerException(String.format("Unable to find SQL script file to execute: %s",
                    sqlScript.getAbsolutePath()), ex);
        }
    }

    @Override
    public void executeScript(final Connection connection, final Reader reader) {
        Validate.notNull(connection, "Connection to execute script on must not be a null object!");
        Validate.notNull(reader, "Reader to read SQL script must not be a null object!");

        final ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);

        try {
            scriptRunner.runScript(reader);
        } catch (IOException | SQLException ex) {
            throw new MariaDBManagerException(String.format("Unable to execute SQL script: %s", ex.getMessage()), ex);
        }
    }

    @Override
    public Connection getConnection() {
        return getConnection(configuration.getDatabase());
    }

    @Override
    public Flyway flyway(String... migrationLocations) {
        return flyway(configuration.getDatabase(), migrationLocations);
    }

    @Override
    public Flyway flyway(String database, String... migrationLocations) {
        return flyway(database, true, migrationLocations);
    }

    @Override
    public Flyway flyway(String database, boolean baseline, String... migrationLocations) {
        final Flyway flyway = new Flyway();

        final String datasource = configuration.getConnectionString(database);
        final String username = configuration.getUsername();
        final String password = configuration.getPassword();

        flyway.setDataSource(datasource, username, password);

        if (baseline) {
            flyway.baseline();
        }

        if (migrationLocations.length != 0) {
            flyway.setLocations(migrationLocations);
        }
        
        return flyway;
    }

    @Override
    public Connection getConnection(String database) {
        Connection connection;

        final String connectionString = configuration.getConnectionString(database);

        try {
            connection = DriverManager.getConnection(connectionString, configuration.getUsername(), configuration.getPassword());
        } catch (SQLException ex) {
            throw new MariaDBManagerException(String.format("Unable to get connection to %s.", connectionString), ex);
        }

        return connection;
    }

    @Override
    public void closeConnection(Connection connection) {
        Validate.notNull(connection, "Connection to close must not be a null object!");

        try {
            connection.close();
        } catch (SQLException ex) {
            logger.severe(String.format("Unable to close SQL connection. Error code: %s. Message: %s.", ex.getErrorCode(), ex.getMessage()));
        }
    }

    @Override
    public MariaDBManagerConfiguration getConfiguration() {
        return configuration;
    }
}
