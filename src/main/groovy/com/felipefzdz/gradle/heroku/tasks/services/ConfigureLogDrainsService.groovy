package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.heroku.api.exception.RequestFailedException
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class ConfigureLogDrainsService {

    static final String RECOVERABLE_ADD_LOG_DRAIN_EXCEPTION = "App hasn't yet been assigned a log channel. Please try again momentarily."

    private final int logDrainRetryWait = 30000
    private final HerokuClient herokuClient

    private final Logger logger

    ConfigureLogDrainsService(HerokuClient herokuClient, Logger logger) {
        this(herokuClient, logger, 30000) // 30 seconds
    }

    ConfigureLogDrainsService(HerokuClient herokuClient, Logger logger, int logDrainRetryWait) {
        this.herokuClient = herokuClient
        this.logger = logger
        this.logDrainRetryWait = logDrainRetryWait
    }

    void configureLogDrains(List<String> logDrains, String appName, int retries) {
        if (logDrains == null || logDrains.isEmpty()) {
            logger.lifecycle "No log drains configured for app $appName"
        } else {
            def drains = herokuClient.listLogDrains(appName)

            logDrains.each { logDrain ->
                def existing = drains.find { it.url == logDrain }

                if (!existing) {
                    try {
                        herokuClient.addLogDrain(appName, logDrain)
                    } catch (RequestFailedException e) {
                        if (e.responseBody.contains(RECOVERABLE_ADD_LOG_DRAIN_EXCEPTION) && retries > 0) {
                            logger.lifecycle "Retrying adding a log drain as Heroku returned: $RECOVERABLE_ADD_LOG_DRAIN_EXCEPTION"
                            sleep(logDrainRetryWait)
                            configureLogDrains(logDrains, appName, --retries)
                        } else {
                            throw e
                        }
                    }
                    logger.lifecycle "Added log drain $logDrain"
                } else {
                    logger.lifecycle "Log drain $logDrain already exists, skipping"
                }
            }
        }
    }
}
