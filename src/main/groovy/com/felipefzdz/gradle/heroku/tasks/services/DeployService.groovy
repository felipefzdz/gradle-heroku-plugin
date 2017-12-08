package com.felipefzdz.gradle.heroku.tasks.services

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuAddon
import com.felipefzdz.gradle.heroku.tasks.model.HerokuWebApp
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class DeployService {

    InstallAddonsService installAddonsService
    HerokuClient herokuClient

    DeployService(InstallAddonsService installAddonsService, HerokuClient herokuClient) {
        this.installAddonsService = installAddonsService
        this.herokuClient = herokuClient
    }

    void deployWeb(HerokuWebApp app, int delayAfterDestroyApp, String apiKey) {
        maybeCreateApplication(app.name, app.teamName, app.recreate, app.stack, app.personalApp, delayAfterDestroyApp)
        installAddons(app.addons.toList(), app.name, apiKey)
        println "Successfully deployed app ${app.name}"
    }

    void maybeCreateApplication(String appName, String teamName, boolean recreate, String stack, boolean personalApp, int delayAfterDestroyApp) {
        boolean exists = herokuClient.appExists(appName)
        if (exists && recreate) {
            herokuClient.destroyApp(appName)
            // Give some time to Heroku to actually destroy the app
            delay(Duration.ofSeconds(delayAfterDestroyApp))
            exists = false
        }
        if (exists) {
            println "App $appName already exists and won't be created"
        } else {
            herokuClient.createApp(appName, teamName, personalApp, stack)
            println "Successfully created app $appName"
        }
    }

    void installAddons(List<HerokuAddon> addons, String appName, String apiKey) {
        installAddonsService.installAddons(addons, apiKey, appName)
    }

    private delay(Duration duration) {
        println "Delaying for ${duration.toMillis()} milliseconds..."
        sleep(duration.toMillis())
    }
}
