package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.Deploy
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)

        project.tasks.create("herokuDeploy", Deploy.class, {
            it.apiKey = extension.apiKey
            it.appName = extension.appName
            it.teamName = extension.teamName
            it.stack = extension.stack
            it.personalApp = extension.personalApp
            it.recreate = extension.recreate
            it.addons = project.extensions.getByName('addons') as NamedDomainObjectContainer<HerokuAddon>
        })

    }
}
