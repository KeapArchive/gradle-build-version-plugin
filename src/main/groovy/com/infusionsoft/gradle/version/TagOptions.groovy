package com.infusionsoft.gradle.version

import static com.google.common.base.Preconditions.*
import static com.google.common.base.Strings.*

class TagOptions {
    private final String versionSplitter
    private final String releaseTagPattern
    private final String groupMatcher
    private final String releaseSuffix
    private final String snapshotSuffix
    private final String releaseTagIfNone

    TagOptions(String versionSplitter,
               String releaseTagPattern,
               String groupMatcher,
               String releaseSuffix,
               String snapshotSuffix,
               String releaseTagIfNone) {
        checkArgument(!isNullOrEmpty(versionSplitter))
        checkArgument(!isNullOrEmpty(releaseTagPattern))
        checkArgument(!isNullOrEmpty(groupMatcher))
        checkNotNull(releaseSuffix)
        checkNotNull(snapshotSuffix)
        checkArgument(!isNullOrEmpty(releaseTagIfNone))
        this.versionSplitter = versionSplitter
        this.releaseTagPattern = releaseTagPattern
        this.groupMatcher = groupMatcher
        this.releaseSuffix = releaseSuffix
        this.snapshotSuffix = snapshotSuffix
        this.releaseTagIfNone = releaseTagIfNone
    }

    String getVersionSplitter() {
        versionSplitter
    }

    String getReleaseTagPattern() {
        releaseTagPattern
    }

    String getGroupMatcher() {
        groupMatcher
    }

    String getReleaseSuffix() {
        releaseSuffix
    }

    String getSnapshotSuffix() {
        snapshotSuffix
    }

    String getReleaseTagIfNone() {
        releaseTagIfNone
    }
}
