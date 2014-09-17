package com.infusionsoft.gradle.version

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildVersionPlugin implements Plugin<Project> {

    private static final RELEASE_PROPERTY_KEY = 'isRelease'

    void apply(Project project) {
        JgitUtil.setJgitToUseSshAgent()

        project.configure(project) {
            BuildVersionExtension extension = extensions.create('buildVersion', BuildVersionExtension)
            extension.setProjectPath(project.rootProject.projectDir.absolutePath)
        }

        project.task('pushTags', type: PushTagsTask) {
            project.tasks.pushTags.inputs.property('projectPath', project.rootProject.projectDir.absolutePath)
        }

        if (System.getProperty(RELEASE_PROPERTY_KEY) && 'true' == (System.getProperty(RELEASE_PROPERTY_KEY))) {
            project.allprojects {
                buildVersion {
                    isRelease = true
                }
            }
        }
    }
}
