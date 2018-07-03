package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.HerokuAppContainer
import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.CreateAppService
import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class CreateBundleTask extends HerokuBaseTask {

    @Internal
    HerokuAppContainer bundle

    private final HerokuClient herokuClient
    private final CreateAppService createAppService

    @Inject
    CreateBundleTask(HerokuClient herokuClient, CreateAppService createAppService) {
        this.herokuClient = herokuClient
        this.createAppService = createAppService
    }

    @TaskAction
    void createBundle() {
        bundle.toList().each { HerokuApp app ->
            createAppService.createApp(app, app.name)
        }
    }
}
