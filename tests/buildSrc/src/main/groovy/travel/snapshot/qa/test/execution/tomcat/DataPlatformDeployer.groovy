package travel.snapshot.qa.test.execution.tomcat

import org.apache.commons.io.IOUtils
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.execution.Execution
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.orchestration.Orchestration
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException
import travel.snapshot.qa.manager.tomcat.TomcatManager
import travel.snapshot.qa.manager.tomcat.docker.TomcatDockerManager
import travel.snapshot.qa.manager.tomcat.docker.TomcatService
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModules
import travel.snapshot.qa.util.Properties
import travel.snapshot.qa.util.Timer

import static travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy.*

/**
 * Deploys modules (wars) to Tomcat instance.
 */
class DataPlatformDeployer {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformDeployer)

    private static final String DEFAULT_TOMCAT_CONTAINER = TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    private final Orchestration orchestration

    private final File dataPlatformDir

    private List<DataPlatformModule> modules = []

    private String containerId = DEFAULT_TOMCAT_CONTAINER

    private DeploymentStrategy deploymentStrategy = DeploymentStrategy.valueOf(Properties.Tomcat.deploymentStrategy)

    DataPlatformDeployer(DataPlatformTestOrchestration orchestration) {
        this.dataPlatformDir = Properties.Location.dataPlatformRepository
        this.orchestration = orchestration.get()
    }

    DataPlatformDeployer strategy(DeploymentStrategy deploymentStrategy) {
        this.deploymentStrategy = deploymentStrategy
        this
    }

    DataPlatformDeployer container(String containerId) {
        this.containerId = containerId
        this
    }

    /**
     * Adds a module to deploy.
     *
     * @param module module to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModule module) {
        if (module) {

            logger.info("Adding {} for deployment.", module.toString())

            this.modules << module
        }
        this
    }

    /**
     * Adds modules for deployment.
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                deploy(module)
            }
        }
        this
    }

    /**
     * Adds modules for deployment.
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(List<DataPlatformModule> modules) {
        for (DataPlatformModule module : modules) {
            deploy(module)
        }
        this
    }

    /**
     * Adds modules for deployment
     *
     * @param modules modules to deploy
     * @return this
     */
    DataPlatformDeployer deploy(DataPlatformModules modules) {
        this.modules.addAll(modules.modules())
        this
    }

    /**
     * Performs actual deployment.
     *
     * @return this
     */
    DataPlatformDeployer execute() {
        final TomcatDockerManager dockerManager = orchestration.getDockerServiceManager(TomcatDockerManager, containerId)
        final TomcatManager manager = dockerManager.getServiceManager()

        if (!dockerManager || !manager) {
            throw new IllegalStateException(
                    String.format("Unable to perform module deployment, managers for container %s are null.", containerId))
        }

        // if there are some modules to be deployed, dependencies of that module have to be deployed as well
        // multiple modules to deploy can depend on the same module, by flatteing these dependencies and filtering
        // duplicities, we get unique dependencies accross all modules
        List<DataPlatformModule> dependencyModules = modules.collect { module -> module.dependencies }.flatten().unique()

        List<DataPlatformModule> modulesToDeploy = []

        modulesToDeploy.addAll(modules)
        modulesToDeploy.addAll(dependencyModules)

        modulesToDeploy.unique().each { module ->

            boolean isDeployed = manager.isDeployed(module.getDeploymentContext())

            switch (deploymentStrategy) {
                case DEPLOYORFAIL:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or fail' deployment strategy.", module.getDeploymentContext()))
                        throw new ContainerDeploymentException(String.format("Unable to deploy %s. Such module is already deployed.", module))
                    }
                    deployModule(module, manager)
                    break
                case DEPLOYORREDEPLOY:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or redeploy' deployment strategy. This deployment is going to be undeployed and " +
                                "deployed again.", module.getDeploymentContext()))
                        undeployModule(module, manager)
                    }
                    deployModule(module, manager)
                    break
                case DEPLOYORSKIP:
                    if (isDeployed) {
                        logger.info(String.format("Deployment of %s is already deployed and you have set " +
                                "'deploy or skip' deployment strategy. This deployment is going to be skipped.",
                                module.getDeploymentContext()))
                        break
                    }
                    deployModule(module, manager)
                    break
                default:
                    throw new IllegalStateException(String.format("Unable to resolve deployment strategy %s.", deploymentStrategy.name()))
            }
        }

        this
    }

    private def deployModule(DataPlatformModule module, TomcatManager manager) {
        File deployment = new File(dataPlatformDir, module.war)

        if (!deployment.exists()) {
            throw new IllegalStateException(String.format("Deployment file %s does not exist.", deployment.absolutePath))
        }

        Timer timer = new Timer()

        logger.info("Deploying of {} module started.", module.getDeploymentContext())

        TomcatContainerLogger loggerTask = Spacelift.task(orchestration, TomcatContainerLogger).container(containerId)
        // we do not await() here, this runs in background so we see Tomcat logs
        Execution<Void> loggerExecution = loggerTask.execute()

        timer.start()

        manager.deploy(deployment.absolutePath)

        timer.stop()

        // here we terminate logging task and close a logging stream
        IOUtils.closeQuietly(loggerTask.getLogOutputStream())
        loggerExecution.terminate()

        logger.info("Deploying of {} module finished in {} seconds.", module.getDeploymentContext(), timer.delta())
    }

    private def undeployModule(DataPlatformModule module, TomcatManager manager) {

        Timer timer = new Timer()

        logger.info("Undeploying of {} module started.", module.getDeploymentContext())

        TomcatContainerLogger loggerTask = Spacelift.task(orchestration, TomcatContainerLogger).container(containerId)
        // we do not await() here, this runs in background so we see Tomcat logs
        Execution<Void> loggerExecution = loggerTask.execute()

        timer.start()

        manager.undeploy(module.war)

        timer.stop()

        // here we terminate logging task and close a logging stream
        IOUtils.closeQuietly(loggerTask.getLogOutputStream())
        loggerExecution.terminate()

        logger.info("Undeploying of {} module finished in {} seconds.", module.getDeploymentContext(), timer.delta())
    }
}
