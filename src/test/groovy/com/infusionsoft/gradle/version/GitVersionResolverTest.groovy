package com.infusionsoft.gradle.version

import org.eclipse.jgit.junit.RepositoryTestCase
import org.junit.Test

import static com.infusionsoft.gradle.version.TestData.defaultTagOptions
import static org.junit.Assert.assertEquals

class GitVersionResolverTest extends RepositoryTestCase {

    @Test
    void testGetVersionNewRelease() {
        String previousVersion = "0.0.5" + defaultTagOptions.releaseSuffix
        String expectedVersion = "0.0.6" + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupOneCommitAheadOfReleaseTag(db, previousVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(true)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionSnapshot() {
        String previousVersion = "0.0.3" + defaultTagOptions.releaseSuffix
        String expectedVersion = "0.0.4" + defaultTagOptions.snapshotSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupOneCommitAheadOfReleaseTag(db, previousVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionExistingRelease() {
        String expectedVersion = "0.0.1" + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupCheckedOutPreviousReleaseTagState(db, expectedVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }
}
