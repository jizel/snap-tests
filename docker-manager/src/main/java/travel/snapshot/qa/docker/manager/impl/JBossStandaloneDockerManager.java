package travel.snapshot.qa.docker.manager.impl;

import org.arquillian.cube.spi.Cube;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.ManagementClient;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.check.JBossStandaloneStartChecker;

public class JBossStandaloneDockerManager extends DockerServiceManager<JBossStandaloneManager> {

    public JBossStandaloneDockerManager(JBossStandaloneManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public Cube start(String containerId) {
        final Task<ManagementClient, Boolean> checkingTask = Spacelift.task(serviceManager.getManagementClient(), JBossStandaloneStartChecker.class);
        return super.start(checkingTask, containerId, ConnectionTimeoutResolver.resolveJBossStandaloneConnectionTimeout(serviceManager.getConfiguration()), 10);
    }

    @Override
    public JBossDomainDockerManager stop(String containerId) {
        serviceManager.closeManagementClient(serviceManager.getManagementClient());
        return super.stop(containerId);
    }

    @Override
    public ServiceType provides() {
        return ServiceType.JBOSS_STANDALONE;
    }
}
