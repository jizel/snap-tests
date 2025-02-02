package travel.snapshot.qa.installation

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.Installation
import org.arquillian.spacelift.gradle.xml.XmlFileLoader
import org.arquillian.spacelift.gradle.xml.XmlTextLoader
import org.arquillian.spacelift.gradle.xml.XmlUpdater
import org.arquillian.spacelift.process.CommandBuilder
import org.arquillian.spacelift.task.Task
import org.arquillian.spacelift.task.TaskFactory
import org.arquillian.spacelift.task.TaskRegistry
import org.arquillian.spacelift.task.archive.UnzipTool
import org.arquillian.spacelift.task.net.DownloadTool
import org.arquillian.spacelift.task.os.CommandTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.ProjectHelper

import java.text.MessageFormat

class MavenInstallation extends BaseContainerizableObject<MavenInstallation> implements Installation {

    private static Logger log = LoggerFactory.getLogger('Maven')

    DeferredValue<String> product = DeferredValue.of(String).from("maven")

    DeferredValue<String> version = DeferredValue.of(String).from("3.3.9")

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from({ getHome().exists() })

    DeferredValue<File> home = DeferredValue.of(File).from("maven")

    DeferredValue<Void> postActions = DeferredValue.of(Void)

    DeferredValue<String> remoteUrl = DeferredValue.of(String).from({
        "http://www.eu.apache.org/dist/maven/maven-3/${getVersion()}/binaries/apache-maven-${getVersion()}-bin.zip"
    })

    DeferredValue<String> fileName = DeferredValue.of(String).from({ "apache-maven-${getVersion()}-bin.zip" })

    DeferredValue<String> alias = DeferredValue.of(String).from("mvn")

    DeferredValue<Map> environment = DeferredValue.of(Map).from([:])

    DeferredValue<File> settingsXml = DeferredValue.of(File).from({
        new File((File) parent['workspace'], "${getAlias()}-settings.xml")
    })

    DeferredValue<File> localRepository = DeferredValue.of(File).from({
        new File((File) parent['workspace'], ".${getAlias()}-repository")
    })

    DeferredValue<Boolean> enableSnapshotNexusSnapshots = DeferredValue.of(Boolean).from(true)

    DeferredValue<Boolean> enableSnapshotNexusStaging = DeferredValue.of(Boolean).from(true)

    DeferredValue<Boolean> enableSnapshotServer = DeferredValue.of(Boolean).from(true)

    MavenInstallation(String name, Object parent) {
        super(name, parent)
    }

    MavenInstallation(String name, MavenInstallation other) {
        super(name, other)

        this.product = other.@product.copy()
        this.version = other.@version.copy()
        this.isInstalled = other.@isInstalled.copy()
        this.home = other.@home.copy()
        this.postActions = other.@postActions.copy()
        this.remoteUrl = other.@remoteUrl.copy()
        this.fileName = other.@fileName.copy()
        this.alias = other.@alias.copy()
        this.environment = other.@environment.copy()
    }

    @Override
    MavenInstallation clone(String name) {
        new MavenInstallation(name, this)
    }

    @Override
    String getProduct() {
        product.resolve()
    }

    @Override
    String getVersion() {
        version.resolve()
    }

    @Override
    boolean isInstalled() {
        isInstalled.resolve()
    }

    @Override
    File getHome() {
        new File(home.resolve(), "apache-maven-" + getVersion())
    }

    String getRemoteUrl() {
        remoteUrl.resolve()
    }

    String getFileName() {
        fileName.resolve()
    }

    String getAlias() {
        alias.resolve()
    }

    Map<String, String> getEnvironment() {
        environment.resolve()
    }

    File getSettingsXml() {
        settingsXml.resolve()
    }

    File getLocalRepository() {
        localRepository.resolve()
    }

    Boolean isEnabledSnapshotNexusSnapshots() {
        enableSnapshotNexusSnapshots.resolve()
    }

    Boolean isEnabledSnapshotNexusStaging() {
        enableSnapshotNexusStaging.resolve()
    }

    Boolean isEnabledSnapshotServer() {
        enableSnapshotServer.resolve()
    }

    @Override
    public void install(Logger logger) {

        File targetFile = getFsPath()

        if (targetFile.exists()) {
            logger.info(":install:${name} Grabbing ${getFileName()} from file system cache")
        } else if (getRemoteUrl()) {
            // ensure parent directory exists
            targetFile.getParentFile().mkdirs()

            // dowload bits if they do not exists
            logger.info(":install:${name} Grabbing from ${getRemoteUrl()}, storing at ${targetFile}")
            Spacelift.task(DownloadTool).from(getRemoteUrl()).timeout(120000).to(targetFile).execute().await()
        }

        logger.info(":install:${name} Extracting installation from ${getFileName()}")
        Spacelift.task(getFsPath(), UnzipTool).toDir(((File) getHome()).parentFile.canonicalFile).execute().await()

        ProjectHelper.project.getAnt().invokeMethod("chmod", [dir: "${getHome()}/bin", perm: "a+x", includes: "*"])

        postActions.resolve()
    }

    @Override
    public void registerTools(TaskRegistry registry) {

        final String mavenAlias = getAlias()

        registry.register(MavenTool, new TaskFactory() {
            Task create() {
                Task task = new MavenTool()
                task.executionService = Spacelift.service()
                return task
            }

            Collection aliases() { [mavenAlias] }
        })

        final String settingsXmlAlias = "settings-${mavenAlias}"

        registry.register(SettingsXmlUpdater, new TaskFactory() {
            Task create() {
                Task task = new SettingsXmlUpdater()
                task.executionService = Spacelift.service()
                return task
            }

            Collection aliases() { [settingsXmlAlias] }
        })

        // create settings.xml with local repository
        SettingsXmlUpdater createSettings = (SettingsXmlUpdater) Spacelift.task(settingsXmlAlias)

        if (isEnabledSnapshotServer()) {
            log.info("Snapshot Nexus Staging server will be added.")
            def properties = ProjectHelper.project.properties
            createSettings.server("nexus-snapshot", properties.get("nexusUser"), properties.get("nexusPassword"))
        }

        if (isEnabledSnapshotNexusStaging()) {
            // here it is named logger, because it is a part of Plugin<Project> implementation
            log.info("Snapshot Nexus Staging repository will be enabled for ${name}")
            createSettings.repository("nexus-repo", new URI("https://nexus.snapshot.travel/content/groups/public"), true)
        }

        if (isEnabledSnapshotNexusSnapshots()) {
            // here it is named logger, because it is a part of Plugin<Project> implementation
            log.info("Snapshots Nexus snapshots repository will be enabled for ${name}")
            createSettings.repository("nexus-repo-snapshots", new URI("https://nexus.snapshot.travel/content/repositories/snapshots"), true).execute().await()
        }

        createSettings.execute().await()
    }

    private File getFsPath() {
        return new File((File) parent['cacheDir'], "${getProduct()}/${getVersion()}/${getFileName()}")
    }

    private Map<String, String> getMavenEnvironmentProperties() {
        Map<String, String> envProperties = new HashMap<String, String>()

        envProperties.put("M2_HOME", getHome().canonicalPath)
        envProperties.put("M2", new File(getHome().canonicalPath, "bin").canonicalPath)
        envProperties.put("MAVEN_OPTS", "-Xms512m -Xmx1024m")
        envProperties.putAll(getEnvironment())
        envProperties
    }

    class MavenTool extends CommandTool {

        DeferredValue<List> nativeCommand = DeferredValue.of(List).ownedBy(this).from([
                linux  : { ["${home}/bin/mvn"] },
                mac    : { ["${home}/bin/mvn"] },
                windows: {
                    [
                            "cmd.exe",
                            "/C",
                            "${home}/bin/mvn.cmd"
                    ]
                },
                solaris: { ["${home}/bin/mvn"] }
        ])

        MavenTool() {
            super()
            List command = nativeCommand.resolve()
            def parameters = System.getenv("TRAVIS") != null ? ["-B", "-q"] : []
            parameters.addAll(["-s", getSettingsXml().getCanonicalPath()])
            parameters.add("-Dorg.apache.maven.user-settings=${getSettingsXml().getCanonicalPath()}")

            this.commandBuilder = new CommandBuilder(command as CharSequence[]).parameters(parameters)
            this.interaction = GradleSpaceliftDelegate.ECHO_OUTPUT

            // we need to use 'super' here, because it means we want to access
            // environment property of CommandTool, we have environment property
            // in MavenInstallation class as well and using 'this' would
            // result in an attempt to cast MavenTool to MavenInstallation which fails

            super.environment.putAll(MavenInstallation.this.getMavenEnvironmentProperties())
        }

        @Override
        public String toString() {
            return "MavenTool (${commandBuilder})"
        }
    }

    class SettingsXmlUpdater extends Task<Object, Void> {

        def static final SERVER_TEMPLATE = '''
            <server>
                <id>{0}</id>
                <username>{1}</username>
                <password>{2}</password>
            </server>
        '''

        def static final PROFILE_TEMPLATE = '''

        <profile>
            <id>{0}</id>

            <repositories>
                <repository>
                    <id>{0}</id>
                    <name>{0}</name>
                    <url>{1}</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>{2}</enabled>
                        <updatePolicy>never</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>
            <!-- plugin repositories are required to fetch dependencies for plugins (e.g. gwt-maven-plugin) -->
            <pluginRepositories>
                <pluginRepository>
                    <id>{0}</id>
                    <name>{0}</name>
                    <url>{1}</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>{2}</enabled>
                        <updatePolicy>never</updatePolicy>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    '''

        def static final PROFILE_ACTIVATION_TEMPLATE = '''
        <activeProfile>{0}</activeProfile>
    '''

        List repositories = []

        List servers = []

        SettingsXmlUpdater() {

            def ant = ProjectHelper.project.ant

            // ensure there is a settings.xml file
            if (!getSettingsXml().exists()) {
                log.warn("No settings.xml file found for Maven installation ${name}, trying to copy the one from default location")
                def defaultFile = new File(new File(System.getProperty("user.home")), '.m2/settings.xml')

                if (!defaultFile.exists()) {
                    log.warn("No settings.xml file found in ${defaultFile.getAbsolutePath()}, using fallback template")
                    defaultFile = File.createTempFile("settings.xml", "-spacelift");
                    defaultFile.deleteOnExit()
                    this.getClass().getResource("/settings.xml-template").withInputStream { ris ->
                        defaultFile.withOutputStream { fos -> fos << ris }
                    }
                }

                ant.copy(file: "${defaultFile}", tofile: "${getSettingsXml()}")
            }
        }

        SettingsXmlUpdater repository(repositoryId, repositoryUri, snapshotsEnabled) {
            repositories.add(["repositoryId": repositoryId, "repositoryUri": repositoryUri, "snapshotsEnabled": snapshotsEnabled])
            this
        }

        SettingsXmlUpdater server(serverId, username, password) {
            servers.add(["serverId" : serverId, "username" : username, "password" : password])
            this
        }

        @Override
        protected Void process(Object input) throws Exception {
            def settings = Spacelift.task(getSettingsXml(), XmlFileLoader).execute().await()

            // in case there is not any 'servers' element, add them to settings.xml
            if (settings.servers.isEmpty()) {
                settings.childern().add(0, new Node(null, "servers"))
            }

            // in case there is not any 'profiles' or 'activeProfiles' element, add them to settings.xml
            if (settings.profiles.isEmpty()) {
                settings.children().add(0, new Node(null, 'profiles'))
            }

            if (settings.activeProfiles.isEmpty()) {
                settings.children().add(0, new Node(null, 'activeProfiles'))
            }

            // servers element
            servers.each { server ->

                println "adding server ${server.serverId}"

                def serverElement = Spacelift.task(MessageFormat.format(SERVER_TEMPLATE, server.serverId, server.username, server.password), XmlTextLoader).execute().await()
                settings.servers.server.findAll { s -> s.id.text() == "${server.serverId}" }.each { it.replaceNode {} }
                settings.servers.each { it.append(serverElement) }
            }

            // update with defined repositories
            repositories.each { r ->
                def profileElement = Spacelift.task(MessageFormat.format(PROFILE_TEMPLATE, r.repositoryId, r.repositoryUri, r.snapshotsEnabled), XmlTextLoader).execute().await()
                def profileActivationElement = Spacelift.task(MessageFormat.format(PROFILE_ACTIVATION_TEMPLATE, r.repositoryId), XmlTextLoader).execute().await()

                // profiles

                // remove previous profiles with the same id
                settings.profiles.profile.findAll { p -> p.id.text() == "${r.repositoryId}" }.each { it.replaceNode {} }
                // append profiles
                settings.profiles.each { it.append(profileElement) }

                // activeProfiles

                // remove previous profile activations
                settings.activeProfiles.activeProfile.findAll { ap -> ap.text() == "${r.repositoryId}" }.each {
                    it.replaceNode {}
                }
                // append profile activations
                settings.activeProfiles.each { it.append(profileActivationElement) }
            }

            // update with local repository
            // delete <localRepository> if present
            settings.localRepository.each { it.replaceNode {} }
            settings.children().add(0, new Node(null, 'localRepository', "${getLocalRepository().getCanonicalPath()}"))

            Spacelift.task(settings, XmlUpdater).file(getSettingsXml()).execute().await()

            null
        }
    }
}