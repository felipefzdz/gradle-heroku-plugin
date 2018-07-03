package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DeployBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    @Internal
    int delayAfterDestroyApp = 20

    @TaskAction
    void deployBundle() {
        bundle.toList()
                .findAll { !it.excludeFromDeployBundle }
                .sort { it.bundlePosition }
                .each { it.deploy(delayAfterDestroyApp) }
    }
}
