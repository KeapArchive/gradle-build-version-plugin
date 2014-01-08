package nz.org.geonet.gradle.build

import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class BuildVersionPluginTest {

    @Test
    void snapShotVersion() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'build-version'
        def pattern = ~/\d+.\d+.\d+-SNAPSHOT/
        Assert.assertTrue(pattern.matcher(project.buildVersion.version).matches())
    }

    @Test
    void releaseVersion() {
        System.setProperty("isRelease", "true")
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'build-version'
        def pattern = ~/\d+.\d+.\d+/
        Assert.assertTrue(pattern.matcher(project.buildVersion.version).matches())
    }

    @Test(expected = GradleScriptException.class)
    void expectErrors() {
        Project project = ProjectBuilder.builder().build()
        project.allprojects {
            project.apply plugin: 'build-version'
            buildVersion {
                releaseTagPattern = "will not match"
            }
        }
        def pattern = ~/\d+.\d+.\d+-SNAPSHOT/
        Assert.assertTrue(pattern.matcher(project.buildVersion.version).matches())
    }

}