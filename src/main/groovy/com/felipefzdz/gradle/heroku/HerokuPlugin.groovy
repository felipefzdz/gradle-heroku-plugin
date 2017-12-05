package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.Deployer
import com.felipefzdz.gradle.heroku.tasks.Destroyer
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        HerokuExtension extension = project.extensions.create("heroku", HerokuExtension.class, project)

        Deployer deployerTask = project.tasks.create("herokuDeploy", Deployer.class)
        deployerTask.apiKey = extension.apiKey
        deployerTask.appName = extension.appName
        deployerTask.teamName = extension.teamName
        deployerTask.personalApp = extension.personalApp

        Destroyer destroyerTask = project.tasks.create("herokuDestroyApp", Destroyer.class)
        destroyerTask.apiKey = extension.apiKey
        destroyerTask.appName = extension.appName
    }
}
