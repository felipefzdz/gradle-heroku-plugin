package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.InstallAddons
import com.felipefzdz.gradle.heroku.tasks.CreateApp
import com.felipefzdz.gradle.heroku.tasks.DestroyApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuBasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        HerokuExtension extension = project.extensions.create('heroku', HerokuExtension, project)
        project.extensions.create('apps', HerokuApp, project)
        project.extensions.add('addons', project.container(HerokuAddon))

        project.tasks.create('herokuCreateApp', CreateApp, {
            it.apiKey = extension.apiKey
            it.appName = extension.appName
            it.teamName = extension.teamName
            it.stack = extension.stack
            it.personalApp = extension.personalApp
        })

        project.tasks.create('herokuDestroyApp', DestroyApp, {
            it.apiKey = extension.apiKey
            it.appName = extension.appName
        })

        project.tasks.create('herokuInstallAddons', InstallAddons, {
            it.apiKey = extension.apiKey
            it.apps = extension.apps
        })
    }
}
