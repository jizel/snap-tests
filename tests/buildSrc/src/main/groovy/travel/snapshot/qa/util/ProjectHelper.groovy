package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.SpaceliftExtension
import org.gradle.api.Project

final class ProjectHelper {

    static boolean isLoadTestRunning() {
        isProfileSelected("loadTests")
    }

    static boolean isDockerUsed() {
        !project.selectedInstallations.collect { installation -> installation.name.startsWith('docker') }.isEmpty()
    }

    static boolean isProfileSelected(String profileName) {
        project.selectedProfile['name'] == profileName
    }

    static boolean isTestSelected(final String testName) {
        !project.selectedTests.findAll { test -> test['name'] == testName }.isEmpty()
    }

    static Project getProject() {
        new GradleSpaceliftDelegate().project()
    }

    static SpaceliftExtension getSpacelift() {
        project.spacelift
    }

    static File getWorkspace() {
        spacelift.workspace
    }
}
