package com.infusionsoft.gradle.version

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.google.common.collect.Lists
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository

class TestData {

    //Just to access the defaults given by the plugin
    private static BuildVersionExtension buildVersionExtension = new BuildVersionExtension()
    static TagOptions defaultTagOptions = new TagOptions(
            buildVersionExtension.versionSplitter,
            buildVersionExtension.releaseTagPattern,
            buildVersionExtension.matchGroup,
            buildVersionExtension.releaseSuffix,
            buildVersionExtension.snapshotSuffix,
            buildVersionExtension.releaseTagIfNone)

    static Repository setupOneCommitAheadOfReleaseTag(Repository repository, String previousVersion) {
        Git git = new Git(repository)
        git.commit().setAuthor("Ratman", "ratman@infusionsoft.com").setMessage("First commit with release tag").call()
        git.tag().setAnnotated(true).setName("release-" + previousVersion).call()
        git.commit().setAuthor("Batman", "batman@gothem.city").setMessage("Second commit that this city deserves").call()
        repository
    }

    static Repository setupCheckedOutPreviousReleaseTagState(Repository repository, String expectedVersion) {
        ArrayList<String> versionElement = Lists.newArrayList(Splitter.on(defaultTagOptions.versionSplitter).split(expectedVersion))
        Integer lastElement = Integer.parseInt(versionElement.remove(versionElement.size() - 1))
        versionElement.add(String.valueOf(lastElement + 1))
        String incrementedVersion = Joiner.on(defaultTagOptions.versionSplitter).join(versionElement)

        Git git = new Git(repository)
        git.commit().setAuthor("Ratman", "ratman@infusionsoft.com").setMessage("First commit with release tag").call()
        git.tag().setAnnotated(true).setName("release-" + expectedVersion).call()
        git.commit().setAuthor("Batman", "batman@gothem.city").setMessage("Second commit that this city deserves").call()
        git.tag().setAnnotated(true).setName("release-" + incrementedVersion).call()
        git.checkout().setName("release-" + expectedVersion).call()
        repository
    }

    static Repository setupNewRepoWithSingleCommit(Repository repository) {
        Git git = new Git(repository)
        git.commit().setAuthor("Ratman", "ratman@infusionsoft.com").setMessage("I'm a single commit").call()
        repository
    }
}
