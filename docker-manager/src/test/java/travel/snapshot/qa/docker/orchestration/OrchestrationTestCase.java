package travel.snapshot.qa.docker.orchestration;

import static org.junit.Assert.assertNotNull;
import static travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQService.DEFAULT_ACTIVEMQ_CONTAINER_ID;
import static travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID;
import static travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBService.DEFAULT_MONGODB_CONTAINER_ID;
import static travel.snapshot.qa.manager.redis.impl.docker.RedisService.DEFAULT_REDIS_CONTAINER_ID;
import static travel.snapshot.qa.manager.tomcat.docker.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID;

import com.github.dockerjava.api.model.Container;
import com.mongodb.MongoClient;
import org.arquillian.cube.ChangeLog;
import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.jboss.shrinkwrap.impl.base.io.tar.TarArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import travel.snapshot.qa.category.OrchestrationTest;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQDockerManager;
import travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQService;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBDockerManager;
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBService;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBDockerManager;
import travel.snapshot.qa.manager.mongodb.impl.docker.MongoDBService;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.impl.docker.RedisDockerManager;
import travel.snapshot.qa.manager.redis.impl.docker.RedisService;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.docker.TomcatDockerManager;
import travel.snapshot.qa.manager.tomcat.docker.TomcatService;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Category(OrchestrationTest.class)
public class OrchestrationTestCase {

    private static final Logger logger = LoggerFactory.getLogger(OrchestrationTestCase.class);

    private static final Orchestration ORCHESTRATION = new Orchestration().start();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void setup() {

        ORCHESTRATION.with(new ActiveMQService().init())
                .with(new MariaDBService().init())
                .with(new MongoDBService().init())
                .with(new TomcatService().init(), new RedisService().init())
                .startServices();
    }

    @AfterClass
    public static void teardown() {
        // just stop a first one
        ORCHESTRATION.stopService(ORCHESTRATION.getStartedContainers().get(0));
        // all other should be stopped
        ORCHESTRATION.stopServices();
        ORCHESTRATION.stop();
    }

    @Test
    public void test() {

        // this should do nothing because all are already started
        ORCHESTRATION.startServices();

        MariaDBManager mariaDBManager = ORCHESTRATION.getDockerServiceManager(MariaDBDockerManager.class, DEFAULT_MARIADB_CONTAINER_ID).getServiceManager();
        ActiveMQManager activeMQManager = ORCHESTRATION.getDockerServiceManager(ActiveMQDockerManager.class, DEFAULT_ACTIVEMQ_CONTAINER_ID).getServiceManager();
        MongoDBManager mongoDBManager = ORCHESTRATION.getDockerServiceManager(MongoDBDockerManager.class, DEFAULT_MONGODB_CONTAINER_ID).getServiceManager();
        TomcatManager tomcatManager = ORCHESTRATION.getDockerServiceManager(TomcatDockerManager.class, DEFAULT_TOMCAT_CONTAINER_ID).getServiceManager();
        RedisManager redisManager = ORCHESTRATION.getDockerServiceManager(RedisDockerManager.class, DEFAULT_REDIS_CONTAINER_ID).getServiceManager();

        // MariaDB

        final Connection sqlConnection = mariaDBManager.getConnection();
        assertNotNull(sqlConnection);
        mariaDBManager.closeConnection(sqlConnection);

        // ActiveMQ

        final javax.jms.Connection activeMQConnection = activeMQManager.buildConnection();
        assertNotNull(activeMQConnection);
        activeMQManager.closeConnection(activeMQConnection);

        // MongoDB

        final MongoClient mongoClient = mongoDBManager.getClient();
        assertNotNull(mongoClient);
        mongoClient.listDatabases().maxTime(1, TimeUnit.MINUTES);
        mongoClient.close();

        final Jedis jedis = redisManager.getJedis();
        assertNotNull(jedis);
        redisManager.close();

        // Tomcat

        Assert.assertTrue(tomcatManager.isRunning());
        // there are already 2 deployments in clean Tomcat - its managers
        Assert.assertEquals(2, tomcatManager.listDeployments().size());
    }

    @Test
    public void ipInspectionTest() {
        Map<String, String> ips = ORCHESTRATION.inspectAllIPs();

        logger.info("Resolved container IPs: {}", ips);

        Assert.assertEquals("There are less resolved IPs than running containers!",
                ORCHESTRATION.getStartedContainers().size(),
                ips.size());

        String tomcatResolvedIpFromMap = ips.get(DEFAULT_TOMCAT_CONTAINER_ID);
        String tomcatResolvedIpFromIPMethod = ORCHESTRATION.inspectIP(DEFAULT_TOMCAT_CONTAINER_ID);

        Assert.assertEquals(tomcatResolvedIpFromMap, tomcatResolvedIpFromIPMethod);

        String mariadbResolvedIp = ips.get(DEFAULT_MARIADB_CONTAINER_ID);

        Assert.assertNotEquals("Some resolved IPs for different containers are same!",
                tomcatResolvedIpFromMap, mariadbResolvedIp);

        List<String> ipAddressesList = new ArrayList<>(ips.values());
        Set<String> ipAddressesSet = new HashSet<>(ipAddressesList);

        Assert.assertEquals("IP addresses are not unique across containers!",
                ipAddressesList.size(), ipAddressesSet.size());
    }

    @Test
    public void clientExecutorTest() throws Exception {
        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        final List<Container> containers = dockerClientExecutor.listRunningContainers();

        for (final Container container : containers) {
            logger.info("container id: {}", container.getId());
            logger.info("container image: {}", container.getImage());
            logger.info("container names: {}", Arrays.asList(container.getNames()));
        }

        dockerClientExecutor.pingDockerServer();

        final List<ChangeLog> changeLogs = dockerClientExecutor.inspectChangesOnContainerFilesystem(DEFAULT_TOMCAT_CONTAINER_ID);

        for (final ChangeLog changeLog : changeLogs) {
            System.out.println(String.format("%s -> %s", changeLog.getKind(), changeLog.getPath()));
        }
    }

    @Test
    @Ignore("be sure to execute this test in a way that system property ${tomcat.mount.dir} gets expanded in arquillian.xml file.")
    public void mountingFolderTestManually() throws Exception {
        final File configDir = testFolder.newFolder("configDirManual");

        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        try (final InputStream inputStream = dockerClientExecutor.getFileOrDirectoryFromContainerAsTar(DEFAULT_TOMCAT_CONTAINER_ID, "/data/tomcat/config")) {
            TarArchive tarArchive = new TarArchive(inputStream);
            tarArchive.extractContents(configDir);
            tarArchive.closeArchive();
        }

        Assert.assertTrue("Archive should contain test.properties file!", new File(configDir, "config/test.properties").exists());
    }

    @Test
    @Ignore("be sure to execute this test in a way that system property ${tomcat.mount.dir} gets expanded in arquillian.xml file.")
    public void mountingFolderTestByAPI() throws Exception {
        final File destinationDir = testFolder.newFolder("configDirByAPI");
        final File sourceDir = new File("/data/tomcat/config");

        ORCHESTRATION.getDockerServiceManager(TomcatDockerManager.class, DEFAULT_TOMCAT_CONTAINER_ID).fetch(sourceDir, destinationDir);

        Assert.assertTrue("Archive should contain test.properties file!", new File(destinationDir, "config/test.properties").exists());
    }
}