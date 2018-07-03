package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class ConfigureLogDrainsService {

    private final HerokuClient herokuClient
    
    private final Logger logger
    
    ConfigureLogDrainsService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void configureLogDrains(List<String> logDrains, String appName) {
        if (logDrains == null || logDrains.isEmpty()) {
            logger.lifecycle "No log drains configured for app $appName"
        } else {
            def drains = herokuClient.listLogDrains(appName)

            logDrains.each { logDrain ->
                def existing = drains.find { it.url == logDrain }

                if (!existing) {
                    herokuClient.addLogDrain(appName, logDrain)
                    logger.lifecycle "Added log drain $logDrain"
                } else {
                    logger.lifecycle "Log drain $logDrain already exists, skipping"
                }
            }
        }
    }
}
