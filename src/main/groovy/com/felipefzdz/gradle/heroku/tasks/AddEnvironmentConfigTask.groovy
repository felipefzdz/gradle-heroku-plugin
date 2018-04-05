package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class AddEnvironmentConfigTask extends DefaultTask {

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @TaskAction
    void addEnviromentConfig() {
        Map<String, String> config = app.herokuConfig.configToBeExpected
        if (config) {
            println "Setting env config variables ${config.keySet().toList().sort()}"
            herokuClient.updateConfig(app.name, config)
            println "Added environment config for $app.name"
        }
    }
}
