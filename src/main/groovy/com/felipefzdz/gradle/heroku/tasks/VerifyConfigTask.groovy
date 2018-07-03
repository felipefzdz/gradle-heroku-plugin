package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import com.felipefzdz.gradle.heroku.tasks.services.VerifyConfigService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class VerifyConfigTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    VerifyConfigService verifyConfigService

    @TaskAction
    void verifyConfig() {
        HerokuConfig config = app.herokuConfig
        if (config) {
            def appName = app.name
            println "Verify config for $appName"
            verifyConfigService.verifyConfig(config, appName)
            println "Verified config for $appName"
        }
    }
}
