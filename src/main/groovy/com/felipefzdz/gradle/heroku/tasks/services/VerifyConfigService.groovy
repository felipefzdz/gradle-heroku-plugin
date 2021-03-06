package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuConfig
import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger

import java.util.regex.Pattern

@CompileStatic
class VerifyConfigService {

    private static final Pattern HEROKU_POSTGRESQL_URL_PATTERN = ~/HEROKU_POSTGRESQL_(.*)_URL/

    private static HerokuClient herokuClient
    private static Logger logger

    VerifyConfigService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void verifyConfig(HerokuConfig config, String appName) {
        def actualConfig = herokuClient.listConfig(appName)
        def configToBeExpected = config.configToBeExpected.findAll { it.value != null }
        def configAddedByHeroku = config.configAddedByHeroku
        def configToBeRemoved = config.configToBeRemoved
        def configToBeAdded = config.configToBeAdded

        def missingConfig = configToBeExpected.keySet() - actualConfig.keySet() - configToBeAdded
        if (missingConfig) {
            logger.lifecycle "Expected config missing for $appName:\n\t" + missingConfig.join("\n\t")
        }

        def unexpectedConfig = actualConfig.keySet() - configToBeExpected.keySet() - configAddedByHeroku - configToBeRemoved
        unexpectedConfig = unexpectedConfig.findAll { varName -> !HEROKU_POSTGRESQL_URL_PATTERN.matcher(varName).matches() }
        if (unexpectedConfig) {
            logger.lifecycle "Unexpected config found for $appName:\n\t" + unexpectedConfig.join("\n\t")
        }

        def secretConfig = configToBeExpected.findAll { it.value == "secret" }.keySet()
        def expectedNonSecretConfig = configToBeExpected.keySet() - secretConfig - configToBeAdded
        def incorrectConfig = expectedNonSecretConfig.findAll { configToBeExpected[it] != actualConfig[it] }
        if (incorrectConfig) {
            logger.lifecycle "Incorrect config found for $appName:\n\t" + incorrectConfig.collect {
                "$it (expected: ${configToBeExpected[it]})"
            }.join("\n\t")
        }

        if (missingConfig || unexpectedConfig || incorrectConfig) {
            throw new GradleException("Heroku config for $appName does not match expected config")
        }
    }

}
