package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.logging.Logger

@CompileStatic
class EnableFeaturesService {

    private final HerokuClient herokuClient
    private final Logger logger

    EnableFeaturesService(HerokuClient herokuClient, Logger logger) {
        this.herokuClient = herokuClient
        this.logger = logger
    }

    void enableFeatures(List<String> features, String appName) {
        if (features) {
            logger.lifecycle "Enabling features $features"
            features.each {
                herokuClient.enableFeature(appName, it)
            }
            logger.lifecycle "Enabled features for $appName"
        }
    }
}
