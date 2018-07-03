package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import com.felipefzdz.gradle.heroku.tasks.services.VerifyConfigService
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class VerifyConfigBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    @Internal
    HerokuClient herokuClient

    @Internal
    VerifyConfigService verifyConfigService

    @Internal
    Logger logger

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
