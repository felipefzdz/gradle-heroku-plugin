package com.felipefzdz.gradle.heroku.tasks

import com.heroku.api.HerokuAPI
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

@CompileStatic
class Destroyer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    HerokuAPI herokuApi

    Destroyer() {
        this.apiKey = project.objects.property(String.class)
        this.appName = project.objects.property(String.class)
    }

    @TaskAction
    def herokuDestroy() {
        herokuApi = new HerokuAPI(apiKey.get())
        logger.quiet("Destroying application ${appName.get()}")
        herokuApi.destroyApp(appName.get())
        logger.quiet("Successfully destroyed app ${appName.get()}")
    }
}

