package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployBundle
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)

        project.tasks.create("herokuDeployBundle", DeployBundle.class, {
            it.apiKey = extension.apiKey
            it.bundle = extension.bundle
        })

    }
}
