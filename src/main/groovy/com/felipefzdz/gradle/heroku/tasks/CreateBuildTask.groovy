package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.CreateBuildService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class CreateBuildTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    private final HerokuClient herokuClient
    private final CreateBuildService createBuildService

    @Inject
    CreateBuildTask(HerokuClient herokuClient, CreateBuildService createBuildService) {
        this.herokuClient = herokuClient
        this.createBuildService = createBuildService
    }

    @TaskAction
    void createBuild() {
        createBuildService.createBuild(app.buildSource, app.name)
    }
}
