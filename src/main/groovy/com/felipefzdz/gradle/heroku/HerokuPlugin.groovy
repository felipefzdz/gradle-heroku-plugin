package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.DeployAppTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigBundleTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.herokuClient
import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.verifyConfigService

@CompileStatic
class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)
        createDeployTasks(extension.bundles, project)
    }

    private static void createDeployTasks(NamedDomainObjectContainer<HerokuAppContainer> bundles, Project project) {
        bundles.all { HerokuAppContainer env ->
            String envName = GUtil.toCamelCase(env.name)
            createBundleTasks(env, project, envName)
        }
    }

    private static void createBundleTasks(HerokuAppContainer bundle, Project project, String envName = '') {
        bundle.all { HerokuApp app ->
            project.tasks.create("herokuDeploy${envName}${GUtil.toCamelCase(app.name)}", DeployAppTask) { DeployAppTask task ->
                task.group = 'deployment'
                task.app = app
                task.herokuClient = herokuClient
                task
            }

            project.tasks.create("herokuVerifyConfigFor${envName}${GUtil.toCamelCase(app.name)}", VerifyConfigTask) { VerifyConfigTask task ->
                task.group = 'deployment'
                task.app = app
                task.verifyConfigService = verifyConfigService
                task
            }
        }

        project.tasks.create("herokuDeploy${envName}Bundle", DeployBundleTask) { DeployBundleTask task ->
            task.group = 'deployment'
            task.bundle = bundle
            task.herokuClient = herokuClient
            task
        }

        project.tasks.create("herokuVerifyConfigFor${envName}Bundle", VerifyConfigBundleTask) { VerifyConfigBundleTask task ->
            task.group = 'deployment'
            task.bundle = bundle
            task.verifyConfigService = verifyConfigService
            task
        }
    }

}
