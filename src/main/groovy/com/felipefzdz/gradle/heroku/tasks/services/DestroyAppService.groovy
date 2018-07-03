package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class DestroyAppService {

    private final HerokuClient herokuClient
    private final Logger logger

    DestroyAppService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void destroyApp(String appName) {
        if (herokuClient.appExists(appName)) {
            herokuClient.destroyApp(appName)
            logger.lifecycle "Successfully destroyed app $appName"
        } else {
            logger.lifecycle "App $appName doesn't exist and won't be destroyed."
        }
    }
}
