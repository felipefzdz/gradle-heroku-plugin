package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.DeployWebTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.herokuClient
import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.verifyConfigService
import static com.felipefzdz.gradle.heroku.utils.FormatUtil.toUpperCamel

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
            project.tasks.create("herokuDeploy${toUpperCamel(app.name)}", DeployWebTask) { DeployWebTask task ->
                task.app = app
                task.herokuClient = herokuClient
            }

            project.tasks.create("herokuVerifyConfigFor${toUpperCamel(app.name)}", VerifyConfigTask) { VerifyConfigTask task ->
                task.app = app
                task.verifyConfigService = verifyConfigService
            }
        }

        project.tasks.create("herokuDeployBundle", DeployBundleTask) { DeployBundleTask task ->
            task.bundle = extension.bundle
            task.herokuClient = herokuClient
        }
    }

}
