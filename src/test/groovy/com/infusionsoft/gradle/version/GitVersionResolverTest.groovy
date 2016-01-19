package com.infusionsoft.gradle.version

import static com.infusionsoft.gradle.version.TestData.defaultTagOptions
import static org.junit.Assert.assertEquals

import org.gradle.api.GradleScriptException
import org.testng.annotations.Test

class GitVersionResolverTest extends TestNGRepositoryTestCase {
    @Test
    void testGetVersionNewRelease() {
        String previousVersion = '0.0.5' + defaultTagOptions.releaseSuffix
        String expectedVersion = '0.0.6' + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupOneCommitAheadOfReleaseTag(db, previousVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(true)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionSnapshot() {
        String previousVersion = '0.0.3' + defaultTagOptions.releaseSuffix
        String expectedVersion = '0.0.4' + defaultTagOptions.snapshotSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupOneCommitAheadOfReleaseTag(db, previousVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionExistingRelease() {
        String tagVersion = '0.0.1'
        String expectedVersion = tagVersion + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupCheckedOutPreviousReleaseTagState(db, tagVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionExistingReleaseWithUncommittedChanges() {
        String tagVersion = '0.0.1'
        String expectedVersion = '0.0.2' + defaultTagOptions.snapshotSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupCheckedOutPreviousReleaseTagWithUncommittedChangesState(db, tagVersion),
                defaultTagOptions)
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionFirstRelease() {
        String expectedVersion = '0.0.1' + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupNewRepoWithSingleCommit(db),
                defaultTagOptions
        )
        String version = gitVersionResolver.getVersion(true)
        assertEquals(expectedVersion, version)
    }

    @Test
    void testGetVersionNoReleaseDeveloperUseCase() {
        String expectedVersion = '0.0.1' + defaultTagOptions.snapshotSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
                TestData.setupNewRepoWithSingleCommit(db),
                defaultTagOptions
        )
        String version = gitVersionResolver.getVersion(false)
        assertEquals(expectedVersion, version)
    }

    @Test(expectedExceptions = GradleScriptException)
    void testGetVersionReleaseWithUncommittedChanges() {
        TestData.setupCheckedOutPreviousReleaseTagWithUncommittedChangesState(db, '0.0.1')
        final gitVersionResolver = new GitVersionResolver(db, defaultTagOptions)
        gitVersionResolver.getVersion(true)
    }

    @Test
    void testGetVersionReleaseHeadTagged() {
        String tagVersion = '0.0.1'
        String expectedVersion = tagVersion + defaultTagOptions.releaseSuffix
        GitVersionResolver gitVersionResolver = new GitVersionResolver(
            TestData.setupCheckedOutPreviousReleaseTagState(db, tagVersion),
            defaultTagOptions)
        String version = gitVersionResolver.getVersion(true)
        assertEquals(expectedVersion, version)
    }
}
