package com.felipefzdz.gradle.heroku.tasks

import com.heroku.api.HerokuAPI
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import static com.felipefzdz.gradle.heroku.heroku.HerokuAPIFactory.create

@CompileStatic
class Destroyer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    HerokuAPI herokuAPI

    Destroyer() {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def herokuDestroy() {
        herokuAPI = create(apiKey.get())
        logger.quiet("Destroying application ${appName.get()}")
        herokuAPI.destroyApp(appName.get())
        logger.quiet("Successfully destroyed app ${appName.get()}")
    }
}

