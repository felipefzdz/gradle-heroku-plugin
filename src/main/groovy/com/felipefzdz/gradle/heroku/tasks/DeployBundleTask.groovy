package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import com.felipefzdz.gradle.heroku.tasks.services.DeployService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DeployBundleTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @Internal
    DeployService deployService

    @Internal
    int delayAfterDestroyApp = 20

    @TaskAction
    void deployBundle() {
        herokuClient.init(apiKey.get())
        bundle.all { HerokuApp app ->
            if (app instanceof HerokuWebApp) {
                deployService.deployWeb(app as HerokuWebApp, delayAfterDestroyApp, apiKey.get())
            }
        }
    }
}