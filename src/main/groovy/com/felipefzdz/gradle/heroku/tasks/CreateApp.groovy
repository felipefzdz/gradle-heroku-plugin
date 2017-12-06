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
        outputs.upToDateWhen { false }
        this.herokuClient = new DefaultHerokuClient(logger)
    }

    @TaskAction
    def herokuCreateApp() {
        herokuClient.init(apiKey.get())
                .createApp(appName.get(), teamName.getOrElse(''), personalApp.get())
        logger.quiet("Successfully created app ${appName.get()}")
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}

