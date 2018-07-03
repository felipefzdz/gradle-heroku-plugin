package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import com.felipefzdz.gradle.heroku.tasks.services.VerifyConfigService
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class VerifyConfigBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    private final VerifyConfigService verifyConfigService
    private final Logger logger

    @Inject
    VerifyConfigBundleTask(VerifyConfigService verifyConfigService, Logger logger) {
        this.verifyConfigService = verifyConfigService
        this.logger = logger
    }

    @TaskAction
    void verifyConfig() {
        bundle.toList().each { HerokuApp app ->
            HerokuConfig config = app.herokuConfig
            if (config) {
                def appName = app.name
                logger.lifecycle "Verify config for $appName"
                verifyConfigService.verifyConfig(config, appName)
                logger.lifecycle "Verified config for $appName"
            }
        }
    }
}
