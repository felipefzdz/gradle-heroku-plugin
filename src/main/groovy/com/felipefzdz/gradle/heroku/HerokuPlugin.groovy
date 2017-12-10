package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import com.felipefzdz.gradle.heroku.tasks.services.DeployService
import com.felipefzdz.gradle.heroku.tasks.DeployWebTask
import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.services.InstallAddonsService
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)
        createDeployTasks(extension, project)
    }

    private static void createDeployTasks(HerokuExtension extension, Project project) {
        HerokuClient herokuClient = new DefaultHerokuClient()
        InstallAddonsService installAddonsService = new InstallAddonsService(herokuClient)
        ConfigureLogDrainsService configureLogDrainsService = new ConfigureLogDrainsService(herokuClient)
        CreateBuildService createBuildService = new CreateBuildService(herokuClient)
        DeployService defaultDeployService = new DeployService(installAddonsService, herokuClient, configureLogDrainsService, createBuildService)

        extension.bundle.all { HerokuApp app ->
            if (app instanceof HerokuWebApp) {
                HerokuWebApp webApp = app as HerokuWebApp
                project.tasks.create("herokuDeploy${app.name.capitalize()}", DeployWebTask) { DeployWebTask task ->
                    task.apiKey = extension.apiKey
                    task.app = webApp
                    task.herokuClient = herokuClient
                    task.deployService = defaultDeployService
                }
            }

        }

        project.tasks.create("herokuDeployBundle", DeployBundleTask) { DeployBundleTask task ->
            task.apiKey = extension.apiKey
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
            task.deployService = defaultDeployService
        }
    }

}
