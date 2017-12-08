package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DestroyAppTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void destroyApp() {
        herokuClient.init(apiKey.get())
        if (herokuClient.appExists(app.name)) {
            herokuClient.destroyApp(app.name)
            println "Successfully destroyed app ${app.name}"
        } else {
            println "App ${app.name} doesn't exist and won't be destroyed."
        }
    }

}
