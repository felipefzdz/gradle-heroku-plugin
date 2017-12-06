package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateApp extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Property<String> appName

    @Internal
    @Optional
    Property<String> teamName

    @Internal
    @Optional
    Property<Boolean> personalApp

    @Internal
    HerokuClient herokuClient

    CreateApp() {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.teamName = project.objects.property(String)
        this.personalApp = project.objects.property(Boolean)
        this.herokuClient = new DefaultHerokuClient()
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def createApp() {
        herokuClient.init(apiKey.get())
        if (herokuClient.appExists(appName.get())) {
            println "App ${appName.get()} already exists and won't be created."
        } else {
            herokuClient.createApp(appName.get(), teamName.getOrElse(''), personalApp.get())
            println "Successfully created app ${appName.get()}"
        }
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setAppName(String appName) {
        this.appName.set(appName)
    }

    void setTeamName(String teamName) {
        this.teamName.set(teamName)
    }

    void setPersonalApp(Boolean personalApp) {
        this.personalApp.set(personalApp)
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setAppName(Property<String> appName) {
        this.appName = appName
    }

    void setTeamName(Property<String> teamName) {
        this.teamName = teamName
    }

    void setPersonalApp(Property<Boolean> personalApp) {
        this.personalApp = personalApp
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}

