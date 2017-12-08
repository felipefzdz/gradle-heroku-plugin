package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.DeployService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DeployWebTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuWebApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    DeployService deployService

    @Internal
    int delayAfterDestroyApp = 20

    @TaskAction
    void deployWeb() {
        herokuClient.init(apiKey.get())
        deployService.deployWeb(app, delayAfterDestroyApp, apiKey.get())
    }

}
