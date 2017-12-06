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
        outputs.upToDateWhen { false }
        this.herokuClient = new DefaultHerokuClient(logger)
    }

    @TaskAction
    def destroyApp() {
        herokuClient.init(apiKey.get())
                .destroyApp(appName.get())
        logger.quiet("Successfully destroyed app ${appName.get()}")
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}

