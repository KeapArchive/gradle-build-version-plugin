package com.infusionsoft.gradle.version

import com.google.common.base.Preconditions
import com.google.common.base.Strings

public class TagOptions {
    private String versionSplitter
    private String releaseTagPattern
    private String groupMatcher
    private String releaseSuffix
    private String snapshotSuffix
    private String releaseTagIfNone

    public TagOptions(String versionSplitter, String releaseTagPattern, String groupMatcher, String releaseSuffix, String snapshotSuffix, String releaseTagIfNone) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSplitter))
        Preconditions.checkArgument(!Strings.isNullOrEmpty(releaseTagPattern))
        Preconditions.checkArgument(!Strings.isNullOrEmpty(groupMatcher))
        Preconditions.checkArgument(!Strings.isNullOrEmpty(releaseSuffix))
        Preconditions.checkArgument(!Strings.isNullOrEmpty(snapshotSuffix))
        Preconditions.checkArgument(!Strings.isNullOrEmpty(releaseTagIfNone))
        this.versionSplitter = versionSplitter
        this.releaseTagPattern = releaseTagPattern
        this.groupMatcher = groupMatcher
        this.releaseSuffix = releaseSuffix
        this.snapshotSuffix = snapshotSuffix
        this.releaseTagIfNone = releaseTagIfNone
    }

    public String getVersionSplitter() {
        return versionSplitter
    }

    public String getReleaseTagPattern() {
        return releaseTagPattern
    }

    public String getGroupMatcher() {
        return groupMatcher
    }

    public String getReleaseSuffix() {
        return releaseSuffix
    }

    public String getSnapshotSuffix() {
        return snapshotSuffix
    }

    public String getReleaseTagIfNone() {
        return releaseTagIfNone
    }
}
