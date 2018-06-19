package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DeployBundleTask extends DefaultTask {

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @Internal
    int delayAfterDestroyApp = 20

    @TaskAction
    void deployBundle() {
        bundle.toList()
                .findAll { !it.excludeFromDeployBundle }
                .each { it.deploy(delayAfterDestroyApp) }
    }
}
