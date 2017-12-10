package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic

@CompileStatic
class EnableFeaturesService {

    HerokuClient herokuClient

    EnableFeaturesService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void enableFeatures(List<String> features, String apiKey, String appName) {
        herokuClient.init(apiKey)
        if (features) {
            println "Enabling features $features"
            features.each {
                herokuClient.enableFeature(appName, it)

            }
            println "Enabled features for $appName"
        }
    }
}
