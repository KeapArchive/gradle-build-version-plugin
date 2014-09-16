package com.infusionsoft.gradle.version

import org.eclipse.jgit.junit.RepositoryTestCase
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class BuildVersionPluginTest extends RepositoryTestCase {

    @Test
    void testBuildVersionPluginAddsPushTagsTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'build-version'

        assertTrue(project.tasks.pushTags instanceof PushTagsTask)
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
