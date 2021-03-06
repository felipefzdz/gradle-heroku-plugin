package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployAppTask
import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigBundleTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil

import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.herokuClient
import static com.felipefzdz.gradle.heroku.dependencyinjection.Graph.verifyConfigService

class HerokuPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply(HerokuBasePlugin)
        HerokuExtension extension = project.extensions.findByType(HerokuExtension)
        createDeployTasks(extension.bundles, project)
    }

    private static void createDeployTasks(NamedDomainObjectContainer<HerokuAppContainer> bundles, Project project) {
        bundles.all { HerokuAppContainer bundle ->
            String bundleName = GUtil.toCamelCase(bundle.name)
            createBundleTasks(bundle, project, bundleName)
        }
    }

    private static void createBundleTasks(HerokuAppContainer bundle, Project project, String bundleName = '') {
        bundle.all { HerokuApp app ->
            def herokuAppTasks = []
            herokuAppTasks << project.tasks.create("herokuDeploy$bundleName${GUtil.toCamelCase(app.name)}", DeployAppTask, herokuClient)
            herokuAppTasks << project.tasks.create("herokuVerifyConfigFor$bundleName${GUtil.toCamelCase(app.name)}", VerifyConfigTask, herokuClient, verifyConfigService, project.logger)
            herokuAppTasks.each { task ->
                task.group = 'deployment'
                task.app = app
            }
        }

        def herokuBundleTasks = []
        herokuBundleTasks << project.tasks.create("herokuDeploy${bundleName}Bundle", DeployBundleTask)
        herokuBundleTasks << project.tasks.create("herokuVerifyConfigFor${bundleName}Bundle", VerifyConfigBundleTask, verifyConfigService, project.logger)
        herokuBundleTasks.each { task ->
            task.group = 'deployment'
            task.bundle = bundle
        }
    }

}
