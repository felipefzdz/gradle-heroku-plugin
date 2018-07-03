package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class AddEnvironmentConfigTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    Logger logger

    @TaskAction
    void addEnviromentConfig() {
        Map<String, String> config = app.herokuConfig.configToBeExpected
        if (config) {
            logger.lifecycle "Setting env config variables ${config.keySet().toList().sort()}"
            herokuClient.updateConfig(app.name, config)
            logger.lifecycle "Added environment config for $app.name"
        }
    }
}
