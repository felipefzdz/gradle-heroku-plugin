package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.Destroyer
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuBasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        HerokuExtension extension = project.extensions.create("heroku", HerokuExtension.class, project)

        project.tasks.create("herokuDestroyApp", Destroyer.class, {
            it.apiKey = extension.apiKey
            it.appName = extension.appName
        })
    }
}
