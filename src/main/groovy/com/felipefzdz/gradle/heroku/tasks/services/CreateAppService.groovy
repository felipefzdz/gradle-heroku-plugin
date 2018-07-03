package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class CreateAppService {

    private final HerokuClient herokuClient

    private final Logger logger

    CreateAppService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void createApp(HerokuApp app, String appName) {
        if (herokuClient.appExists(appName)) {
            logger.lifecycle "App $appName already exists and won't be created."
        } else {
            herokuClient.createApp(appName, app.teamName, app.personalApp, app.stack)
            logger.lifecycle "Successfully created app $appName"
        }
    }
}
