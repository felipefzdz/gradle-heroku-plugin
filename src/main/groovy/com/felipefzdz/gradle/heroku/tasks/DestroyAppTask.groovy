package com.felipefzdz.gradle.heroku.tasks

import com.felipefzdz.gradle.heroku.heroku.HerokuClient
import com.felipefzdz.gradle.heroku.tasks.model.HerokuApp
import com.felipefzdz.gradle.heroku.tasks.services.DestroyAppService
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

@CompileStatic
class DestroyAppTask extends HerokuBaseTask {

    @Internal
    HerokuApp app

    private final HerokuClient herokuClient
    private final DestroyAppService destroyAppService

    @Inject
    DestroyAppTask(HerokuClient herokuClient, DestroyAppService destroyAppService) {
        this.herokuClient = herokuClient
        this.destroyAppService = destroyAppService
    }

    @TaskAction
    void destroyApp() {
        destroyAppService.destroyApp(app.name)
    }

}
