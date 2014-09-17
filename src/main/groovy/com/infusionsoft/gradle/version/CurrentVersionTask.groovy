package com.infusionsoft.gradle.version

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CurrentVersionTask extends DefaultTask {

    @TaskAction
    def currentVersion() {
        println 'currentVersion:' + project.buildVersion.version
    }
}
