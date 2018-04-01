package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.DeployWebTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.deployService
import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.herokuClient

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)
        createDeployTasks(extension, project)
    }

    private static void createDeployTasks(HerokuExtension extension, Project project) {
        extension.bundle.all { HerokuApp app ->
            project.tasks.create("herokuDeploy${app.name.capitalize()}", DeployWebTask) { DeployWebTask task ->
                task.apiKey = extension.apiKey
                task.app = app
                task.herokuClient = herokuClient
                task.deployService = deployService
            }
        }

        project.tasks.create("herokuDeployBundle", DeployBundleTask) { DeployBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
            task.deployService = deployService
        }
    }

}
