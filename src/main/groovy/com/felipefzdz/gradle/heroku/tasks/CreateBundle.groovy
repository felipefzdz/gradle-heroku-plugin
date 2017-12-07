package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateBundle extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Collection<HerokuApp> bundle

    @Internal
    HerokuClient herokuClient

    CreateBundle() {
        this.apiKey = project.objects.property(String)
        this.bundle = project.objects.listProperty(HerokuApp) as List<HerokuApp>
        this.herokuClient = new DefaultHerokuClient()
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def createApp() {
        herokuClient.init(apiKey.get())
        bundle.each { HerokuApp app ->
            if (herokuClient.appExists(app.name)) {
                println "App ${app.name} already exists and won't be created."
            } else {
                herokuClient.createApp(app.name, app.teamName, app.personalApp, app.stack)
                println "Successfully created app ${app.name}"
            }
        }
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setBundle(Collection<HerokuApp> bundle) {
        this.bundle = bundle
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}

