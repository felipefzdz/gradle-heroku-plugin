package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.InstallAddons
import com.felipefzdz.gradle.heroku.tasks.CreateBundle
import com.felipefzdz.gradle.heroku.tasks.DestroyBundle
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
        project.extensions.create('bundle', HerokuApp, project)
        project.extensions.add('addons', project.container(HerokuAddon))

        project.tasks.create('herokuCreateBundle', CreateBundle, {
            it.apiKey = extension.apiKey
            it.bundle = extension.bundle
        })

        project.tasks.create('herokuDestroyBundle', DestroyBundle, {
            it.apiKey = extension.apiKey
            it.bundle = extension.bundle
        })

        project.tasks.create('herokuInstallAddons', InstallAddons, {
            it.apiKey = extension.apiKey
            it.bundle = extension.bundle
        })
    }
}
