package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class DeployAppTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    @Internal
    int delayAfterDestroyApp = 20

    private final HerokuClient herokuClient

    @Inject
    DeployAppTask(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    @TaskAction
    void deployWeb() {
        app.deploy(delayAfterDestroyApp)
    }

}
