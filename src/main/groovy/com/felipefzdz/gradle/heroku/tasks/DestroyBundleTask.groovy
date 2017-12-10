package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DestroyBundleTask extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void destroyBundle() {
        herokuClient.init(apiKey.get())
        bundle.toList().reverse().each { HerokuApp app ->
            if (herokuClient.appExists(app.name)) {
                herokuClient.destroyApp(app.name)
                println "Successfully destroyed app ${app.name}"
            } else {
                println "App ${app.name} doesn't exist and won't be destroyed."
            }
        }
    }
}
