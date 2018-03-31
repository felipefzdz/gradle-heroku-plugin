package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic

@CompileStatic
class CreateAppService {

    HerokuClient herokuClient

    CreateAppService(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void createApp(HerokuApp app, String apiKey, String appName) {
        herokuClient.init(apiKey)
        if (herokuClient.appExists(appName)) {
            println "App $appName already exists and won't be created."
        } else {
            herokuClient.createApp(appName, app.teamName, app.personalApp, app.stack)
            println "Successfully created app $appName"
        }
    }
}
