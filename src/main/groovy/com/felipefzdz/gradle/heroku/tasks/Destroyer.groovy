package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.DefaultHerokuClient
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction


@CompileStatic
class Destroyer extends DefaultTask {

    Property<String> apiKey
    Property<String> appName
    HerokuClient herokuClient

    Destroyer() {
        outputs.upToDateWhen { false }
        this.herokuClient = new DefaultHerokuClient(logger)
    }

    @TaskAction
    def herokuDestroy() {
        herokuClient.init(apiKey.get())
                .destroyApp(appName.get())
    }

    void setHerokuClient(HerokuClient herokuClient) {
        this.herokuClient = herokuClient
    }
}

