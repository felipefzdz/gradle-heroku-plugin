package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateBundleTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void createBundle() {
        herokuClient.init(apiKey.get())
        bundle.toList().reverse().each { HerokuApp app ->
            if (herokuClient.appExists(app.name)) {
                println "App ${app.name} already exists and won't be created."
            } else {
                herokuClient.createApp(app.name, app.teamName, app.personalApp, app.stack)
                println "Successfully created app ${app.name}"
            }
        }
    }
}
