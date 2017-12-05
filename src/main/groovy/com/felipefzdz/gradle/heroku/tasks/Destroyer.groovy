package com.felipefzdz.gradle.heroku.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import static com.felipefzdz.gradle.heroku.heroku.HerokuClientFactory.create

@CompileStatic
class Destroyer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName

    Destroyer() {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def herokuDestroy() {
        create(logger, apiKey.get())
                .destroyApp(appName.get())
    }
}

