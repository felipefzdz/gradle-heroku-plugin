package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.Deployer
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)

        project.tasks.create("herokuDeploy", Deployer.class, {
            it.apiKey = extension.apiKey
            it.appName = extension.appName
            it.teamName = extension.teamName
            it.personalApp = extension.personalApp
            it.recreate = extension.recreate
        })

    }
}
