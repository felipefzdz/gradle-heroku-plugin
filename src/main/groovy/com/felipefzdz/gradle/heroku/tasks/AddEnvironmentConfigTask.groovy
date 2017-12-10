package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class AddEnvironmentConfigTask extends DefaultTask {
    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void addEnviromentConfig() {
        herokuClient.init(apiKey.get())
        if (app.config) {
            println "Setting env config variables ${app.config.keySet().toList().sort()}"
            herokuClient.updateConfig(app.name, app.config)
            println "Added environment config for $app.name"
        }
    }
}
