package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic

@CompileStatic
class DestroyAppService {

    HerokuClient herokuClient

    DestroyAppService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void destroyApp(String apiKey, String appName) {
        herokuClient.init(apiKey)
        if (herokuClient.appExists(appName)) {
            herokuClient.destroyApp(appName)
            println "Successfully destroyed app $appName"
        } else {
            println "App $appName doesn't exist and won't be destroyed."
        }
    }
}
