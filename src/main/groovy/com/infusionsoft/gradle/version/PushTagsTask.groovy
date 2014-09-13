package com.infusionsoft.gradle.version

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PushTagsTask extends DefaultTask {

    @TaskAction
    def pushTags() {
        Repository repo = new FileRepositoryBuilder()
                .setWorkTree(new File(project.getRootProject().projectDir.absolutePath))
                .findGitDir()
                .build()
        Git git = new Git(repo)
        git.push().setPushTags().call()
    }
}
