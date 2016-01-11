package com.infusionsoft.gradle.version

import static org.junit.Assert.assertTrue

import org.eclipse.jgit.junit.RepositoryTestCase
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class BuildVersionPluginTest extends RepositoryTestCase {
    @BeforeMethod
    public void setUp() throws Exception {
        // superclass uses Junit @Before
        super.setUp();
    }

    @Test
    void testBuildVersionPluginAddsPushTagsTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'build-version'

        assertTrue(project.tasks.pushTags instanceof PushTagsTask)
    }

    @Test
    void testBuildVersionPluginAddsCurrentVersionTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'build-version'

        assertTrue(project.tasks.currentVersion instanceof CurrentVersionTask)
    }

    @Test
    void testBuildVersionPluginSetsIsReleaseWhenSystemArgumentPassed() {
        Project project = ProjectBuilder.builder().build()
        System.setProperty("isRelease", "true")
        project.apply plugin: 'build-version'

        boolean isRelease = project.buildVersion.isRelease
        boolean isReleaseAllProjects = project.allprojects.buildVersion.isRelease

        assertTrue(isRelease)
        assertTrue(isReleaseAllProjects)
    }

    @Test
    void testSnapShotVersion() {
        System.setProperty("isRelease", "false")
        Project project = ProjectBuilder.builder().withProjectDir(db.directory.parentFile).build()
        project.apply plugin: 'build-version'
        def pattern = ~/\d+.\d+.\d+-SNAPSHOT/
        assertTrue(pattern.matcher(project.buildVersion.version).matches())
    }

    @Test
    void testReleaseVersion() {
        System.setProperty("isRelease", "true")
        TestData.setupNewRepoWithSingleCommit(db)
        Project project = ProjectBuilder.builder().withProjectDir(db.directory.parentFile).build()
        project.apply plugin: 'build-version'
        def pattern = ~/\d+.\d+.\d+/
        assertTrue(pattern.matcher(project.buildVersion.version).matches())
    }
}
