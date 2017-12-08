package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateAppTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void createApp() {
        herokuClient.init(apiKey.get())
        if (herokuClient.appExists(app.name)) {
            println "App ${app.name} already exists and won't be created."
        } else {
            herokuClient.createApp(app.name, app.teamName, app.personalApp, app.stack)
            println "Successfully created app ${app.name}"
        }
    }

}
