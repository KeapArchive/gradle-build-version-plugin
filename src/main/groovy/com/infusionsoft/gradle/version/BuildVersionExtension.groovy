package com.infusionsoft.gradle.version

class BuildVersionExtension {

    String releaseTagPattern = "^release-(\\d+\\.\\d+\\.\\d+)"
    String matchGroup = "\$1"
    String versionSplitter = "."
    String snapshotSuffix = "-SNAPSHOT"
    String releaseSuffix = ""
    String releaseTagIfNone = "release-0.0.0"
    boolean isRelease = false

    private String projectPath;

    void setProjectPath(String projectPath) {
        this.projectPath = projectPath
    }

    String getVersion() {
        TagOptions tagSomething = new TagOptions(
                versionSplitter,
                releaseTagPattern,
                matchGroup,
                releaseSuffix,
                snapshotSuffix,
                releaseTagIfNone)
        GitVersionResolver gitVersionResolver = new GitVersionResolver(projectPath, tagSomething)
        String version = gitVersionResolver.getVersion(isRelease)

        return version
    }

}
