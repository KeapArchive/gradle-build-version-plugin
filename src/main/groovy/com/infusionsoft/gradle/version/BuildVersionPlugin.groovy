package com.infusionsoft.gradle.version

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildVersionPlugin implements Plugin<Project> {

    private static final RELEASE_PROPERTY_KEY = 'isRelease'
    private static final TASK_GROUP = 'Build Version'

    void apply(Project project) {
        JgitUtil.setJgitToUseSshAgent()

        project.configure(project) {
            BuildVersionExtension extension = extensions.create('buildVersion', BuildVersionExtension)
            extension.setProjectPath(project.rootProject.projectDir.absolutePath)
        }

        project.task('pushTags', type: PushTagsTask, group: TASK_GROUP, description: 'Pushes all tags on local working git clone to remote origin')
        project.task('currentVersion', type: CurrentVersionTask, group: TASK_GROUP, description: 'Prints the current version as detected by the current state of the git repository')

        if (System.getProperty(RELEASE_PROPERTY_KEY) && 'true' == (System.getProperty(RELEASE_PROPERTY_KEY))) {
            project.allprojects {
                buildVersion {
                    isRelease = true
                }
            }
        }
    }
}
