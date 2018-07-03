package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.CreateAppService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class CreateAppTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    private final HerokuClient herokuClient

    private final CreateAppService createAppService

    @Inject
    CreateAppTask(HerokuClient herokuClient, CreateAppService createAppService) {
        this.herokuClient = herokuClient
        this.createAppService = createAppService
    }

    @TaskAction
    void createApp() {
        createAppService.createApp(app, app.name)
    }

}
