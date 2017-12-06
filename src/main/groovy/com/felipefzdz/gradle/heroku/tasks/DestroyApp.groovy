package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction


@CompileStatic
class DestroyApp extends DefaultTask {

    @Internal
    Property<String> apiKey

    @Internal
    Property<String> appName

    @Internal
    HerokuClient herokuClient

    DestroyApp() {
        this.apiKey = project.objects.property(String)
        this.appName = project.objects.property(String)
        this.herokuClient = new DefaultHerokuClient()
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def destroyApp() {
        herokuClient.init(apiKey.get())
        if (herokuClient.appExists(appName.get())) {
            herokuClient.destroyApp(appName.get())
            println "Successfully destroyed app ${appName.get()}"
        } else {
            println "App ${appName.get()} doesn't exist and won't be destroyed."
        }
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }

    void setApiKey(String apiKey) {
        this.apiKey.set(apiKey)
    }

    void setAppName(String appName) {
        this.appName.set(appName)
    }

    void setApiKey(Property<String> apiKey) {
        this.apiKey = apiKey
    }

    void setAppName(Property<String> appName) {
        this.appName = appName
    }

}

