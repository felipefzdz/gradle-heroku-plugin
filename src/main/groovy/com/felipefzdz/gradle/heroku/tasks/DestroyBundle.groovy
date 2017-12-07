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
class DestroyBundle extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Collection<HerokuApp> bundle

    @Internal
    HerokuClient herokuClient

    DestroyBundle() {
        this.apiKey = project.objects.property(String)
        this.bundle = project.objects.listProperty(HerokuApp) as List<HerokuApp>
        this.herokuClient = new DefaultHerokuClient()
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def destroyApp() {
        herokuClient.init(apiKey.get())
        bundle.each { HerokuApp app ->
            if (herokuClient.appExists(app.name)) {
                herokuClient.destroyApp(app.name)
                println "Successfully destroyed app ${app.name}"
            } else {
                println "App ${app.name} doesn't exist and won't be destroyed."
            }
        }
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
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
}

