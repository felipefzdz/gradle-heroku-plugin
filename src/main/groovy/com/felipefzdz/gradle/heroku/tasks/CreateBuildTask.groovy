package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.ConfigureLogDrainsService
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateBuildTask extends DefaultTask {
    @Internal
    Property<String> apiKey

    @Internal
    HerokuApp app

    @Internal
    HerokuClient herokuClient

    @Internal
    CreateBuildService createBuildService

    @TaskAction
    void createBuild() {
        herokuClient.init(apiKey.get())
        createBuildService.createBuild(app.buildSource, apiKey.get(), app.name)
    }
}
