package com.felipefzdz.gradle.heroku

import com.felipefzdz.gradle.heroku.tasks.DeployBundleTask
import com.felipefzdz.gradle.heroku.tasks.DeployWebTask
import com.felipefzdz.gradle.heroku.tasks.VerifyConfigTask
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuEnv
import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
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
        createDeployTasks(extension.bundles, extension.bundle, project)
    }

    private static void createDeployTasks(NamedDomainObjectContainer<HerokuEnv> bundles, HerokuAppContainer bundle, Project project) {
        bundles.all { HerokuEnv env ->
            String envName = env.name.capitalize()
            createBundleTasks(env.bundle, project, envName)
        }
        createBundleTasks(bundle, project)
    }

    private static void createBundleTasks(HerokuAppContainer bundle, Project project, String envName = '') {
        bundle.all { HerokuApp app ->
            project.tasks.create("herokuDeploy${envName}${toUpperCamel(app.name)}", DeployWebTask) { DeployWebTask task ->
                task.app = app
                task.herokuClient = herokuClient
                task
            }

            project.tasks.create("herokuVerifyConfigFor${envName}${toUpperCamel(app.name)}", VerifyConfigTask) { VerifyConfigTask task ->
                task.app = app
                task.verifyConfigService = verifyConfigService
                task
            }
        }

        project.tasks.create("herokuDeploy${envName}Bundle", DeployBundleTask) { DeployBundleTask task ->
            task.bundle = bundle
            task.herokuClient = herokuClient
            task
        }
    }

}
