package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic

@CompileStatic
class ConfigureLogDrainsService {

    HerokuClient herokuClient

    ConfigureLogDrainsService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void configureLogDrains(List<String> logDrains, String apiKey, String appName) {
        herokuClient.init(apiKey)
        if (logDrains == null || logDrains.isEmpty()) {
            println "No log drains configured for app $appName"
        } else {
            def drains = herokuClient.listLogDrains(appName)

            logDrains.each { logDrain ->
                def existing = drains.find { it.url == logDrain }

                if (!existing) {
                    herokuClient.addLogDrain(appName, logDrain)
                    println "Added log drain $logDrain"
                } else {
                    println "Log drain $logDrain already exists, skipping"
                }
            }
        }
    }
}
