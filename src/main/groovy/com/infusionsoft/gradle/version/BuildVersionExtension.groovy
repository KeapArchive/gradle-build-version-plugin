package com.infusionsoft.gradle.version

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

class BuildVersionExtension {

    String releaseTagPattern = '^release-(\\d+\\.\\d+\\.\\d+)'
    String matchGroup = "\$1"
    String versionSplitter = '.'
    String snapshotSuffix = '-SNAPSHOT'
    String releaseSuffix = ''
    String releaseTagIfNone = 'release-0.0.0'
    boolean isRelease = false

    private String projectPath

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
        Repository repo = new FileRepositoryBuilder()
                .findGitDir(new File(projectPath))
                .build()
        GitVersionResolver gitVersionResolver = new GitVersionResolver(repo, tagSomething)
        gitVersionResolver.getVersion(isRelease)
    }

}
