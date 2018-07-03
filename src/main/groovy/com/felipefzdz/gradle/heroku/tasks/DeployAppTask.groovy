package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DeployAppTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    int delayAfterDestroyApp = 20

    @TaskAction
    void deployWeb() {
        app.deploy(delayAfterDestroyApp)
    }

}
