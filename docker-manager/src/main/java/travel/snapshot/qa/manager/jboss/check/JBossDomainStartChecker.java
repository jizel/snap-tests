package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.task.Task;
import org.jboss.as.arquillian.container.domain.ManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if a JBoss container in a domain mode is started or not by checking its state by domain management client.
 *
 * @see ManagementClient#isDomainInRunningState()
 */
public class JBossDomainStartChecker extends Task<ManagementClient, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainStartChecker.class);

    @Override
    protected Boolean process(ManagementClient managementClient) throws Exception {

        try {
            return managementClient.isDomainInRunningState();
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
        }

        return false;
    }
}
