package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import com.felipefzdz.gradle.heroku.tasks.services.VerifyConfigService
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class VerifyConfigTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    private final HerokuClient herokuClient
    private final VerifyConfigService verifyConfigService
    private final Logger logger

    @Inject
    VerifyConfigTask(HerokuClient herokuClient, VerifyConfigService verifyConfigService, Logger logger) {
        this.herokuClient = herokuClient
        this.verifyConfigService = verifyConfigService
        this.logger = logger
    }

    @TaskAction
    void verifyConfig() {
        HerokuConfig config = app.herokuConfig
        if (config) {
            def appName = app.name
            logger.lifecycle "Verify config for $appName"
            verifyConfigService.verifyConfig(config, appName)
            logger.lifecycle "Verified config for $appName"
        }
    }
}
